$(document).ready(function () {
  var $canvas = $("#statsChart");
  if ($canvas.length === 0) return;

  var deckCount = parseInt($canvas.attr("data-deck")) || 0;
  var flashcardCount = parseInt($canvas.attr("data-flashcard")) || 0;

  console.log("Deck Count:", deckCount);
  console.log("Flashcard Count:", flashcardCount);

  var data = {
    labels: ["Bộ thẻ", "Flashcard"],
    datasets: [
      {
        label: "Số lượng",
        data: [deckCount, flashcardCount],
        backgroundColor: ["#198754", "#ffc107"],
        borderRadius: 6,
      },
    ],
  };

  var ctx = $canvas[0].getContext("2d");
  new Chart(ctx, {
    type: "bar",
    data: data,
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        title: {
          display: true,
          text: "Thống kê số lượng tạo mới",
          font: { size: 16 },
        },
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0,
          },
        },
      },
    },
  });
});
