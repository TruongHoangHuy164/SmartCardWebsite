let index = 0;
let correctCount = 0;

function shuffle(array) {
    return array.sort(() => Math.random() - 0.5);
}

function updateProgress() {
    const total = flashcards.length;
    const percent = ((index + 1) / total) * 100;
    $('#progressBar').css('width', `${percent}%`);
    $('#progressText').text(`${index + 1} / ${total} — Đúng: ${correctCount}`);
}

function showFlashcard(i) {
    const card = flashcards[i];
    $('#term').text(card.term);
    $('#correctAnswerText')?.remove(); // Xóa dòng đúng cũ nếu có

    if (card.imageUrl && card.imageUrl.trim() !== "") {
        $('#image').attr('src', card.imageUrl).removeClass('d-none');
    } else {
        $('#image').addClass('d-none');
    }

    const correct = card.definition;
    const wrongDefs = flashcards
        .filter((f, idx) => idx !== i && f.definition && f.definition !== correct)
        .map(f => f.definition);

    shuffle(wrongDefs);
    const choices = shuffle([correct, ...wrongDefs.slice(0, 3)]);

    const labels = ['A', 'B', 'C', 'D'];
    const correctIndex = choices.indexOf(correct);
    const correctLabel = labels[correctIndex];

    const $options = $('#options').empty();
    choices.forEach((choice, idx) => {
        const label = labels[idx];
        const $col = $('<div class="col"></div>');
        const $btn = $(`<button class="btn btn-outline-secondary btn-answer w-100">${label}. ${choice}</button>`);

        $btn.on('click', function () {
            if (choice === correct) {
                correctCount++;
                $(this).removeClass('btn-outline-secondary').addClass('btn-success')
                    .html(`<i class="bi bi-check-circle-fill me-2"></i>${label}. ${choice}`);
            } else {
                $(this).removeClass('btn-outline-secondary').addClass('btn-danger')
                    .html(`<i class="bi bi-x-circle-fill me-2"></i>${label}. ${choice}`);

                // ✅ Hiện dòng đáp án đúng
                const answerText = `Đáp án đúng là: ${correctLabel}. ${correct}`;
                const $correct = $(`<p id="correctAnswerText" class="mt-3 text-success fw-bold">${answerText}</p>`);
                $('#options').after($correct);
            }

            $('#options button').prop('disabled', true);
            updateProgress();
        });

        $col.append($btn);
        $options.append($col);
    });

    updateProgress();
}

$('#nextBtn').on('click', () => {
    if (index < flashcards.length - 1) {
        index++;
        showFlashcard(index);
    } else {
        // Lấy deckId từ URL hiện tại
        const currentUrl = window.location.href;
        const deckIdMatch = currentUrl.match(/\/deck\/(\d+)/);
        const deckId = deckIdMatch ? deckIdMatch[1] : '';

        // ✅ Hiển thị SweetAlert khi hoàn thành
        Swal.fire({
            title: '🎉 Hoàn thành!',
            text: `Bạn đã trả lời đúng ${correctCount} / ${flashcards.length} câu.`,
            icon: 'success',
            confirmButtonText: 'Quay lại bộ thẻ'
        }).then(() => {
            window.location.href = `/decks/${deckId}`;
        });
    }
});

$(document).ready(() => {
    if (flashcards.length > 0) {
        shuffle(flashcards);
        showFlashcard(0);
    } else {
        $('#term').text("Không có flashcard nào.");
        $('#nextBtn').hide();
    }
});
