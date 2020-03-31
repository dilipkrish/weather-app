import React from 'react';
import './App.css';
import WeatherSearch from "./components/WeatherSearch";
import SearchResults from "./components/SearchResults";
import TemperatureGraph from "./components/TemperatureGraph";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            weather: {
                city: '',
                state: '',
                weatherStations: [],
                selectedStation: '',
                temperatures: []
            }
        };

        this.onSubmit = this.onSubmit.bind(this);
    }

    onSubmit(e) {
        const {city, state} = this.state.weather;
        e.preventDefault();
        const uri = "http://localhost:8080/api/weather/stations/" + state + "/" + city + "";
        console.log(uri)
        fetch(uri)
            .then(res => res.json())
            .then((d) => {
                    console.log('Number of weather stations: ' + d.length);
                    this.setState({
                        weather: {
                            ...this.state.weather,
                            weatherStations: d
                        },
                        isLoaded: true
                    });
                },
                (error) => {
                    this.setState({
                        ...this.state.weather,
                        isLoaded: true,
                        error
                    });
                });
    };

    onCityChanged = (e) => {
        this.setState({
            weather: {
                ...this.state.weather,
                city: e.target.value
            }
        });
    };

    onStationChanged = (e) => {
        const uri = "http://localhost:8080/api/weather/temperatures/" + e.value + "";
        fetch(uri)
            .then(res => res.json())
            .then((d) => {
                    console.log('Number of rows: ' + d.length);
                    this.setState({
                        weather: {
                            ...this.state.weather,
                            temperatures: d,
                            selectedStation: e
                        },
                        isLoaded: true
                    });

                },
                (error) => {
                    this.setState({
                        weather: {
                            ...this.state.weather,
                            selectedStation: e
                        },
                        isLoaded: true,
                        error
                    });
                });

    };

    onStateChanged = (e) => {
        this.setState({
            weather: {
                ...this.state.weather,
                state: e.target.value
            }
        });
    };

    render() {
        let temperatureGraph;
        if (this.state.weather.temperatures.length > 0) {
            temperatureGraph = <TemperatureGraph temperatures={this.state.weather.temperatures} />
        }
        return (
            <div className="App">
                <h1>Weather App</h1>
                <WeatherSearch
                    weather={this.state.weather}
                    onCityChanged={this.onCityChanged}
                    onStateChanged={this.onStateChanged}
                    onSubmit={this.onSubmit.bind(this)}
                />
                <SearchResults
                    weatherStations={this.state.weather.weatherStations}
                    onStationChanged={this.onStationChanged}
                    selectedStation={this.state.weather.selectedStation}
                />
                {temperatureGraph}
            </div>
        );
    }
}

export default App;
