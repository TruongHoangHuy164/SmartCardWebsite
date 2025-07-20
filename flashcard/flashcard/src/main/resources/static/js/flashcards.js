function fetchImages() {
    const term = $('#term').val().trim();
    const def = $('#definition').val().trim();
    const query = term || def;

    if (!query) {
        $('#imageRow').html('');
        return;
    }

    const accessKey = 'fSrSfPC-Sg15szRsP4HlTMS-iszLfpVUlRAof_vQidU';
    const url = `https://api.unsplash.com/search/photos?query=${encodeURIComponent(query)}&per_page=12&client_id=${accessKey}`;

    console.log(url);
    $.getJSON(url, function (data) {
        if (data.results.length > 0) {
            const imagesHtml = data.results.map(img => `
                <div class="col-4 col-md-3 col-lg-2">
                    <div class="image-option" data-url="${img.urls.small}">
                        <img src="${img.urls.small}" alt="Ảnh">
                    </div>
                </div>
            `).join('');
            $('#imageRow').html(imagesHtml);
        } else {
            $('#imageRow').html('<p class="text-muted">Không tìm thấy ảnh phù hợp.</p>');
        }
    }).fail(function () {
        $('#imageRow').html('<p class="text-danger">Không thể tải ảnh. Vui lòng thử lại sau.</p>');
    });
}

$(document).ready(function () {
    let debounceTimer;
    $('#term, #definition').on('input', function () {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(fetchImages, 500);
    });

    $('#imageRow').on('click', '.image-option', function () {
        $('.image-option').removeClass('selected');
        $(this).addClass('selected');

        const selectedUrl = $(this).data('url');

        // ✅ Gán URL ảnh vào input hidden
        $('#imageUrlFromUnsplash').val(selectedUrl);

        // ✅ Xóa ảnh upload nếu chọn ảnh Unsplash
        $('#imageFile').val('');
    });

    // Kiểm tra kích thước file
    $('#imageFile').on('change', function () {
        const file = this.files[0];
        if (file && file.size > 2 * 1024 * 1024) {
            alert('Ảnh tải lên phải nhỏ hơn 2MB.');
            this.value = '';
        } else {
            // ✅ Nếu người dùng chọn ảnh upload, bỏ chọn ảnh Unsplash
            $('.image-option').removeClass('selected');
            $('#imageUrlFromUnsplash').val('');
        }
    });
});
