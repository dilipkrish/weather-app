import React from 'react';
import './../App.css';
import PropTypes from 'prop-types';
import CanvasJSReact from '../assets/canvasjs.react';
const CanvasJSChart = CanvasJSReact.CanvasJSChart;

class TemperatureGraph extends React.Component {
    constructor() {
        super();
        this.generateDataPoints = this.generateDataPoints.bind(this);
    }

    generateDataPoints(temperatures, accessor) {
        const dps = [];
        for(let i = 0; i < temperatures.length; i++) {
            dps.push({label: temperatures[i].date, y: accessor(temperatures[i])});
        }
        return dps;
    }
    toggleDataSeries(e){
        if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
            e.dataSeries.visible = false;
        }
        else{
            e.dataSeries.visible = true;
        }
        this.chart.render();
    }

    render() {
        const options = {
            theme: "light2",
            subtitles: [{
                text: "Click Legend to Hide or Unhide Data Series"
            }],
            axisX: {
                title: "States"
            },
            axisY: {
                title: "Temperature in F",
                titleFontColor: "#0205ad",
                lineColor: "#0205ad",
                labelFontColor: "#0205ad",
                tickColor: "#0205ad",
                includeZero: false
            },
            toolTip: {
                shared: true
            },
            legend: {
                cursor: "pointer",
                itemclick: this.toggleDataSeries.bind(this)
            },
            animationEnabled: true,
            zoomEnabled: true,
            title: {
                text: "Try Zooming and Panning"
            },
            data: [{
                type: "spline",
                name: "High ℉",
                showInLegend: true,
                yValueFormatString: "##0 F",
                dataPoints: this.generateDataPoints(this.props.temperatures, t => t.high)
            },
                {
                    type: "spline",
                    name: "Low ℉",
                    showInLegend: true,
                    yValueFormatString: "##0 F",
                    dataPoints: this.generateDataPoints(this.props.temperatures, t => t.low)
                }]
        };

        return (
            <div>
                <CanvasJSChart options={options}
                    onRef={ref => this.chart = ref}
                />
            </div>
        );
    }
}
TemperatureGraph.propTypes = {
    temperatures: PropTypes.array.isRequired
};

export default TemperatureGraph;

