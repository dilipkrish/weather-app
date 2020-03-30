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

    generateDataPoints(temperatures) {
        const dps = [];
        for(let i = 0; i < temperatures; i++) {
            dps.push({label: temperatures[i].date,y: temperatures[i].high});
        }
        return dps;
    }

    render() {
        const options = {
            theme: "light2", // "light1", "dark1", "dark2"
            animationEnabled: true,
            zoomEnabled: true,
            title: {
                text: "Try Zooming and Panning"
            },
            axisY: {
                includeZero: false
            },
            data: [{
                type: "area",
                dataPoints: this.generateDataPoints(this.props.temperatures)
            }]
        };

        return (
            <div>
                <CanvasJSChart options={options}
                    /* onRef={ref => this.chart = ref} */
                />
                {/*You can get reference to the chart instance as shown above using onRef. This allows you to access all chart properties and methods*/}
            </div>
        );
    }
}
TemperatureGraph.propTypes = {
    temperatures: PropTypes.array.isRequired
};

export default TemperatureGraph;

