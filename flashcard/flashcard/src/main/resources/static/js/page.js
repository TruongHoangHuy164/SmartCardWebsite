// page.js - Xử lý chuyển trang Ajax cho phân trang deck
$(document).on('click', '.deck-pagination', function(e) {
    e.preventDefault();
    var url = $(this).attr('href');
    if (!url) return;
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            var newContent = $(data).find('#deckListContainer').html();
            $('#deckListContainer').html(newContent);
        },
        error: function() {
            alert('Đã xảy ra lỗi khi chuyển trang.');
        }
    });
});
