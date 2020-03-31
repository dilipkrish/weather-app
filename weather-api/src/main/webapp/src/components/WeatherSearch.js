import React from 'react';
import './../App.css';
import PropTypes from 'prop-types';

class WeatherSearch extends React.Component {

    render() {
        return (
            <div className="App">
                <form onSubmit={this.props.onSubmit}>
                    <label>City</label><input name="city"  type="text" onChange={this.props.onCityChanged}/>
                    <label>State</label><input name="state"  type="text" placeholder="Two letter state" onChange={this.props.onStateChanged}/>
                    <input type="submit" value="submit" />
                </form>
            </div>
        );
    }
}

WeatherSearch.propTypes = {
    weather: PropTypes.object.isRequired
};

export default WeatherSearch;
