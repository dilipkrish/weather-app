import React from 'react';
import './../App.css';
import PropTypes from 'prop-types';
import RadioButton from "./RadioButton";

class SearchResults extends React.Component {
    render() {
        const radios = this.props.weatherStations.map((ws) => (
        <RadioButton
            key={ws.stationId}
            id={ws.stationId}
            onChange={this.props.onStationChanged}
            value={ws.stationId}
            isSelected={ws.stationId === this.props.selectedStation}
            label={ws.name}/>

        ));
        return (
            <form>
                {radios}
            </form>
        )
    }
}

SearchResults.propTypes = {
    weatherStations: PropTypes.array.isRequired,
    selectedStation: PropTypes.string.isRequired,
    onStationChanged: PropTypes.func.isRequired
};

export default SearchResults;
