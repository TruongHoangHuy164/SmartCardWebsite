function loadDecks() {
  const keyword = $("#searchInput").val();
  const sort = $("#sortSelect").val();
  const sub = $("#subjectSelect").val();

  console.log(keyword);
  console.log(sort);
  console.log(sub);

  $("#loadingIndicator").show();
  $("#deckListContainer").hide();

  $.ajax({
    url: "/decks/search",
    type: "GET",
    data: { keyword, sort, sub },
    success: function (html) {
      $("#deckListContainer").html(html).show();
    },
    error: function (xhr) {
      alert("❌ Lỗi khi tải dữ liệu:\n" + xhr.responseText);
    },
    complete: function () {
      $("#loadingIndicator").hide();
    },
  });
}

function getParameterByName(name) {
  const url = new URL(window.location.href);
  return url.searchParams.get(name);
}

$(document).ready(function () {

  $("#searchInput").on("input", function () {
    loadDecks();
  });

  $("#sortSelect").on("change", function () {
    loadDecks();
  });

  $("#subjectSelect").on("change", function () {
    loadDecks();
  });

  // ✅ GẮN sự kiện đúng cách với nội dung động
  $(document).on("click", "#createDeckLink", function (e) {
    e.preventDefault();

    var subject = $("#subjectSelect").val();

    var url = "/decks/create?subject=" + encodeURIComponent(subject);
    window.location.href = url;
  });

  const subjectFromUrl = getParameterByName("subject");

  if (subjectFromUrl) {
    $("#subject").val(subjectFromUrl);

    if (subjectFromUrl === "Khác") {
      $("#customSubjectWrapper").show();
    } else {
      $("#customSubjectWrapper").hide();
    }
  }

  // Khi người dùng chọn lại trong select
  $(document).on("change", "#subject", function () {
    const selected = $(this).val();
    if (selected === "Khác") {
      $("#customSubjectWrapper").show();
    } else {
      $("#customSubjectWrapper").hide();
      $("#customSubject").val("");
    }
  });

  $("#createDeckForm").on("submit", function () {
    // Nếu chọn "Khác" thì ghi đè giá trị của select bằng input
    if ($("#subject").val() === "Khác") {
      const custom = $("#customSubject").val().trim();
      if (custom) {
        $("#subject").append(
          `<option value="${custom}" selected>${custom}</option>`
        );
      }
    }
  });
});
