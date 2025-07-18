let index = 0;
let correctCount = 0;

function shuffle(array) {
    let currentIndex = array.length, randomIndex;
    while (currentIndex !== 0) {
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex--;
        [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
    }
    return array;
}

function updateProgress() {
    const total = quizQuestions.length;
    const percent = ((index + 1) / total) * 100;
    $('#progressBar').css('width', `${percent}%`);
    $('#progressText').text(`${index + 1} / ${total} — Đúng: ${correctCount}`);

    if (index === total - 1) {
        $('#finishBtn').removeClass('d-none');
        $('#nextBtn').addClass('d-none');
    }
}

function showFlashcard(i) {
    const card = quizQuestions[i];
    $('#term').text(card.questionText);
    $('#correctAnswerText').remove();
    $('#image').addClass('d-none');

    const correct = card.correctAnswer;
    const wrongDefs = quizQuestions
        .filter((f, idx) => idx !== i && f.correctAnswer && f.correctAnswer !== correct)
        .map(f => f.correctAnswer);

    shuffle(wrongDefs);
    const choices = shuffle([correct, ...wrongDefs.slice(0, 3)]);
    const labels = ['A', 'B', 'C', 'D'];
    const correctIndex = choices.indexOf(correct);
    const correctLabel = labels[correctIndex];

    const $options = $('#options').empty();

    choices.forEach((choice, idx) => {
        const label = labels[idx];
        const $col = $('<div class="col"></div>');
        const $btn = $(`<button class="btn btn-outline-secondary btn-answer w-100 fs-5 py-3">${label}. ${choice}</button>`);

        $btn.on('click', function () {
            const isCorrect = (choice === correct);
            const userAnswer = choice;

            if (isCorrect) {
                correctCount++;
                $(this).removeClass('btn-outline-secondary').addClass('btn-success')
                    .html(`<i class="bi bi-check-circle-fill me-2"></i>${label}. ${choice}`);
            } else {
                $(this)
                    .removeClass('btn-outline-secondary')
                    .addClass('fw-bold')
                    .css({ backgroundColor: '#D30015FF', color: 'white', borderColor: '#D8081DFF' })
                    .html(`<i class="bi bi-x-circle-fill me-2"></i>${label}. ${choice}`);

                const answerText = `Đáp án đúng là: ${correctLabel}. ${correct}`;
                const $correct = $(`<p id="correctAnswerText" class="mt-3 text-success fw-bold">${answerText}</p>`);
                $('#options').after($correct);
            }

            $('#options button').prop('disabled', true);

            fetch('/study/save-result', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({
                    quizId: quizId,
                    questionId: card.id,
                    userAnswer: userAnswer,
                    isCorrect: isCorrect ? 1 : 0
                })
            });

            updateProgress();
        });

        $col.append($btn);
        $options.append($col);
    });

    updateProgress();
}

$('#nextBtn').on('click', () => {
    if (index < quizQuestions.length - 1) {
        index++;
        showFlashcard(index);
    }
});

$('#finishBtn').on('click', () => {
    const currentUrl = window.location.href;
    const deckIdMatch = currentUrl.match(/\/deck\/(\d+)/);
    const deckId = deckIdMatch ? deckIdMatch[1] : '';

    Swal.fire({
        title: '🎉 Hoàn thành!',
        text: `Bạn đã trả lời đúng ${correctCount} / ${quizQuestions.length} câu.`,
        icon: 'success',
        confirmButtonText: 'Quay lại bộ thẻ'
    }).then(() => {
        window.location.href = `/decks/${deckId}`;
    });

    fetch('/study/complete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
            quizId: quizId,
            score: (correctCount / quizQuestions.length) * 10,
            correctAnswers: correctCount,
            totalQuestions: quizQuestions.length
        })
    });
});

$(document).ready(() => {
    try {
        if (typeof quizQuestions === 'string') {
            quizQuestions = JSON.parse(quizQuestions);
        }
    } catch (e) {
        console.error("❌ Không parse được quizQuestions:", e);
        quizQuestions = [];
    }

    if (quizQuestions.length > 0) {
        shuffle(quizQuestions);
        // quizQuestions = quizQuestions.slice(0, 10);
        showFlashcard(0);
    } else {
        $('#term').text("Không có câu hỏi nào.");
        $('#nextBtn').hide();
    }
});
