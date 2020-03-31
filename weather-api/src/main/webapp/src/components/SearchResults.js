import React from 'react';
import './../App.css';
import PropTypes from 'prop-types';
import Dropdown from 'react-dropdown';
import 'react-dropdown/style.css';

class SearchResults extends React.Component {
    render() {

        if (this.props.weatherStations.length > 0) {
            return (
                <Dropdown
                    options={this.props.weatherStations.map(ws => ({value: ws.stationId, label: ws.name}))}
                    onChange={this.props.onStationChanged}
                    value={this.props.selectedStation}
                    placeholder="Select an weather station"/>
            )
        } else {
            return ('Please enter a valid city, state.');
        }
    }
}

SearchResults.propTypes = {
    weatherStations: PropTypes.array.isRequired,
    selectedStation: PropTypes.string.isRequired,
    onStationChanged: PropTypes.func.isRequired
};

export default SearchResults;
