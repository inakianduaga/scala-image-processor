var ScalaDemo = (function() {

    var RANDOM_IMAGE_SIZE = {
        //width: 1000,
        //height: 600,
        width: 200,
        height: 100,
    };

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

    //var uploadFile = (url, blobOrFile, success) => {
    //
    //    var xhr = new XMLHttpRequest();
    //    xhr.open('POST', url, true);
    //    xhr.onload = (e) => {
    //        console.log(e);
    //        success(e);
    //    };
    //
    //    xhr.send(blobOrFile);
    //};

    var uploadFile = (url, blobOrFile, success) => {
        var formData = new FormData();

//// HTML file input, chosen by user
//        formData.append("userfile", fileInputElement.files[0]);

// JavaScript file-like object
//        var content = '<a id="a"><b id="b">hey!</b></a>'; // the body of the new file...
//        var blob = new Blob([content], { type: "text/xml"});

        formData.append("fileInput", blobOrFile);

        var request = new XMLHttpRequest();
        request.open("POST", url);
        request.send(formData);
        request.addEventListener('load', () => success(request.response));

    }


    var imageFromBinary = (blob) => {
        var img = document.createElement('img');
        img.src = URL.createObjectURL(blob);
        return img;
    };

    var getLastDownloadedBinary = () => lastDownloadedBinary;

    return {
        downloadFile,
        imageFromBinary,
        RANDOM_IMAGE_URL,
        SERVER_URL_ENDPOINT,
        getLastDownloadedBinary,
        uploadFile,
    }
})();

// Dom bindings

$('body').on('click', '#fetchImage', () => {
    ScalaDemo.downloadFile(ScalaDemo.RANDOM_IMAGE_URL, (binary) => {
        $('#imageContainer').html(ScalaDemo.imageFromBinary(binary));
    })
});

$('body').on('click', '#submitDownloaded', () => {
    ScalaDemo.uploadFile(ScalaDemo.SERVER_URL_ENDPOINT, ScalaDemo.getLastDownloadedBinary(), (e) => {
        //$('#imageContainer').html(ScalaDemo.imageFromBinary(binary));
        console.log(e);
        //console.log(e.response);

    })
});

$('body').on('click', '#manualUploadSubmit', (e) => {
    e.preventDefault();
    var fileToUpload = $('#fileToUpload')[0].files[0];

    ScalaDemo.uploadFile(ScalaDemo.SERVER_URL_ENDPOINT, fileToUpload, (e) => {
        //$('#imageContainer').html(ScalaDemo.imageFromBinary(binary));
        console.log(e);
        //console.log(e.response);

    })
});


