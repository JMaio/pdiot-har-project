import React from 'react';
// import logo from './logo.svg';
// import './App.css';
// import './c3.css';
// import { PdiotChart } from './components/PdiotChart';
// import { PdiotChartNew } from './components/PdiotChartNew';
// import { PdiotChartRT } from './components/PdiotChartRT';
import { PdiotChartVis } from './components/PdiotChartVis';

function App() {
  return (
    <div className="App">
      {/* < /> */}
      <header className="App-header">
        {/* <PdiotChart /> */}
        <PdiotChartVis />
        {/* <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a> */}
      </header>
    </div>
  );
}

export default App;
