var ScalaDemo = (function() {

    var RANDOM_IMAGE_SIZE = {
        //width: 1000,
        //height: 600,
        width: 600,
        height: 400,
    };

    var galleryPlaceholder = $('#resultsPlaceholder').eq(0);

    var RANDOM_IMAGE_URL = 'https://unsplash.it/'+RANDOM_IMAGE_SIZE.width+'/+'+RANDOM_IMAGE_SIZE.height+'?random';

    var SERVER_URL_ENDPOINT = '/image';

    var lastDownloadedBinary;

    var downloadFile = function downloadFile(url, success) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.responseType = "blob";
        xhr.onreadystatechange = () => {
            if (xhr.readyState == 4) {
                if (success) {
                    lastDownloadedBinary = xhr.response;
                    success(xhr.response);
                }
            }
        };
        xhr.send(null);
    };

    var uploadFile = (url, blobOrFile, success) => {
        var formData = new FormData();

        formData.append("fileInput", blobOrFile);

        var request = new XMLHttpRequest();
        request.open("POST", url);
        request.send(formData);
        request.addEventListener('load', () => success(JSON.parse(request.response)));

    }


    var imageFromBinary = (blob) => {
        var img = document.createElement('img');
        img.src = URL.createObjectURL(blob);
        return img;
    };

    var getLastDownloadedBinary = () => lastDownloadedBinary;

    var generateResultsGallery = (imageList) => {
        imageList
            //.map(path => '<img src="'+path+'" />')
            .forEach(path => $(galleryPlaceholder).prepend($('<img>', {src: path})) );

        $(galleryPlaceholder).collagePlus();
    };

    var generateImageMarkup = () => {

    }

    return {
        RANDOM_IMAGE_URL,
        SERVER_URL_ENDPOINT,
        downloadFile,
        imageFromBinary,
        getLastDownloadedBinary,
        uploadFile,
        generateResultsGallery
    }
})();

// Dom bindings

$('body').on('click', '#fetchImage', () => {
    ScalaDemo.downloadFile(ScalaDemo.RANDOM_IMAGE_URL, (binary) => {
        $('#imageContainer').html(ScalaDemo.imageFromBinary(binary));
    })
});

$('body').on('click', '#submitDownloaded', () => {
    ScalaDemo.uploadFile(ScalaDemo.SERVER_URL_ENDPOINT, ScalaDemo.getLastDownloadedBinary(), ScalaDemo.generateResultsGallery);
});

$('body').on('click', '#manualUploadSubmit', (e) => {
    e.preventDefault();
    var fileToUpload = $('#fileToUpload')[0].files[0];
    ScalaDemo.uploadFile(ScalaDemo.SERVER_URL_ENDPOINT, fileToUpload, ScalaDemo.generateResultsGallery);
});


