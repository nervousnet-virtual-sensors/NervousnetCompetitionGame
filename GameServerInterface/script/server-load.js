function init() {
    initHost(0);
    initHost(1);
}

var seriesOptions = [
    {
        strokeStyle: 'rgba(255, 0, 0, 1)',
        fillStyle: 'rgba(255, 0, 0, 0.1)',
        lineWidth: 3
    },
    {
        strokeStyle: 'rgba(0, 255, 0, 1)',
        fillStyle: 'rgba(0, 255, 0, 0.1)',
        lineWidth: 3
    }
];

var sensorTimeLines = [
    new SmoothieChart({
        millisPerPixel: 20,
        grid: {
            strokeStyle: '#555555',
            lineWidth: 1,
            millisPerLine: 1000,
            verticalSections: 4
        }
    }), new SmoothieChart({
        millisPerPixel: 20,
        grid: {
            strokeStyle: '#555555',
            lineWidth: 1,
            millisPerLine: 1000,
            verticalSections: 4
        }
    })

];


function initHost(hostId) {

    // Initialize an empty TimeSeries for each CPU.
    var cpuDataSets = [new TimeSeries(), new TimeSeries()];

    var now = new Date().getTime();
    for (var t = now - 1000 * 50; t <= now; t += 1000) {
        addRandomValueToDataSets(t, cpuDataSets);
    }
    // Every second, simulate a new set of readings being taken from each CPU.
    setInterval(function () {
        addRandomValueToDataSets(new Date().getTime(), cpuDataSets);
    }, 1000);

    for (var i = 0; i < cpuDataSets.length; i++) {
        sensorTimeLines[hostId].addTimeSeries(cpuDataSets[i], seriesOptions[i]);
    }

    sensorTimeLines[hostId].streamTo(document.getElementById('sensor' + hostId), 1000);
}

function addRandomValueToDataSets(time, dataSets) {
    for (var i = 0; i < dataSets.length; i++) {
        //dataSets[i].append(time, Math.random());
        dataSets[i].append(time, Math.floor((Math.random() * 20000) + 1));
    }
}

function stopChart(id) {
    sensorTimeLines[id].stop();
}

function runChart(id) {
    sensorTimeLines[id].start();
}