// ============
// API
// ============

var ScalaDemo = (() => {

    var RANDOM_IMAGE_SIZE = {
        width: 1000,
        height: 800,
        //width: 800,
        //height: 500,
    };

    var multiThreaded = true;

    var galleryPlaceholder = $('#resultsPlaceholder').eq(0);

    var RANDOM_IMAGE_URL = 'https://unsplash.it/'+RANDOM_IMAGE_SIZE.width+'/+'+RANDOM_IMAGE_SIZE.height+'?random';

    var SERVER_URL_ENDPOINTS = {
        single: '/image/single',
        threaded: 'image/threaded',
        websocket: '/ws'
    };

    var getServerEndpoint = () => multiThreaded ? SERVER_URL_ENDPOINTS.threaded : SERVER_URL_ENDPOINTS.single;

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
    };

    var imageFromBinary = (blob) => {
        var img = document.createElement('img');
        img.src = URL.createObjectURL(blob);
        return img;
    };

    var getLastDownloadedBinary = () => lastDownloadedBinary;

    var generateResultsGallery = (imageList) => {
        stopTimer();
        $(galleryPlaceholder).html('');
        imageList
            .map(path => '<div class="col col-xs-12 col-md-3"><a href="'+path+'" target="_blank"><img src="'+path+'"></a></div>')
            .forEach(html => $(galleryPlaceholder).prepend($(html)));
    };

    var toggleThreadOption = () => multiThreaded = !multiThreaded;

    var setThreadOption = (threaded) => multiThreaded = threaded;

    var getTimerDomId = () => multiThreaded ? '#multiThreadTimer' : '#singleThreadTimer';

    var startTimer = () => $(getTimerDomId()).TimeCircles().destroy() && $(getTimerDomId()).TimeCircles({
        "time": {
            "Days": {
                "show": false
            },
            "Hours": {
                "show": false
            },
            "Minutes": {
                "text": "Minutes",
                "color": "#BBFFBB",
                "show": true
            },
            "Seconds": {
                "text": "Seconds",
                "color": "#FF9999",
                "show": true
            }
        }
    });

    var stopTimer = () => $(getTimerDomId()).TimeCircles().stop();

    /**
     * Websocket communication stream
     */
    var websocket = (() => {
        var ws = new WebSocket(`ws://${ window.location.hostname}:${ window.location.port }${ SERVER_URL_ENDPOINTS.websocket }`);

        ws.onopen = function () {
            console.log("Connection opened");
        };

        ws.onclose = function () {
            console.log("Connection is closed...");
        };

        var serverStream = Bacon.fromEventTarget(ws, "message").map(function(event) {
            var dataString = event.data;
            return JSON.parse(dataString);
        });

        return {
            serverStream
        };
    })();

    return {
        RANDOM_IMAGE_URL,
        getServerEndpoint,
        downloadFile,
        imageFromBinary,
        getLastDownloadedBinary,
        uploadFile,
        generateResultsGallery,
        toggleThreadOption,
        setThreadOption,
        startTimer,
        stopTimer,
        websocket,
    }
})();


// ============
// Dom bindings
// ============

$('body').on('click', '#fetchImage', () => {
    ScalaDemo.downloadFile(ScalaDemo.RANDOM_IMAGE_URL, (binary) => {
        $('#imageContainer').html(ScalaDemo.imageFromBinary(binary));
        $('#submitDownloaded').removeClass('hide');
    });
});

$('body').on('click', '#submitDownloaded', () => {
    ScalaDemo.uploadFile(ScalaDemo.getServerEndpoint(), ScalaDemo.getLastDownloadedBinary(), ScalaDemo.generateResultsGallery);
});

$('body').on('click', '#manualUploadSubmit', (e) => {
    e.preventDefault();
    var fileToUpload = $('#fileToUpload')[0].files[0];
    ScalaDemo.uploadFile(ScalaDemo.getServerEndpoint(), fileToUpload, ScalaDemo.generateResultsGallery);
});

$('body').on('click', '#submitDownloaded, #manualUploadSubmit', (e) => {
    ScalaDemo.startTimer();
});

$('body').on('click', '.threadSelector', function() {
    ScalaDemo.setThreadOption($(this).data('threaded'));
    $('.progressWrapper').addClass('hide').filter('.'+$(this).data('target')).removeClass('hide');
});

// Regenerate gallery every time the server pushes an update
ScalaDemo.websocket.serverStream.onValue(ScalaDemo.generateResultsGallery);
