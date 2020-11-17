// @ts-nocheck
import React, { useEffect, useMemo, useRef, useState } from "react";
import { DefaultApi } from "../openapiClient";
import { CanvasJSChart } from "canvasjs-react-charts";
import oboe from "oboe";
// import {
//   ChartContainer,
//   ChartRow,
//   YAxis,
//   Charts,
//   LineChart,
// } from "react-timeseries-charts";
import Ring from "ringjs";
// import { TimeRange, TimeSeries } from "pondjs";
// import RTChart from "react-rt-chart";

import {
  XYPlot,
  XAxis,
  YAxis,
  ChartLabel,
  HorizontalGridLines,
  VerticalGridLines,
  LineSeries,
  LineSeriesCanvas,
} from "react-vis";

const ws = 50;

export const PdiotChartVis = (props: any): JSX.Element => {
  const api = useMemo(() => new DefaultApi(), []);
  const xData = new Ring(ws);
  const yData = new Ring(ws);
  const zData = new Ring(ws);
  xData.push([0] * ws);
  yData.push([0] * ws);
  zData.push([0] * ws);

  // const events = new Ring(200),
  //   const series = new TimeSeries({
  //     name: "Respeck",
  //     columns: ["x"],
  //     xData
  //   });

  const [xGraphData, setXGraphData] = useState([]);
  const [yGraphData, setYGraphData] = useState([]);
  const [zGraphData, setZGraphData] = useState([]);
  // const timeRange = new TimeRange(0, ws)

  // const chart = useRef();
  const [seq, setSeq] = useState(0);

  // const respeckMac = "E9:01:E8:22:92:C8".replaceAll(':', '-')
  // const respeckMac = "E9-01-E8-22-92-C8"; //.replaceAll(':', '-')
  const respeckMac = "C0-FF-EE"; //.replaceAll(':', '-')
  const host = "http://localhost:5000/api/v1";

  const ringToDataPoints = (r) => r.toArray().map((d, i) => ({ x: i, y: d }));

  useEffect(() => {
    api.getRespeckStreamedData(respeckMac).then(r => {
      console.log(r)
    })
  })
  //   useEffect(() => {
  //     // var s = 0;
  //     // const xData = []
  //     // const yData = []
  //     // const zData = []
  //     // const lineArr = [];
  //     // console.log(xData)
  //     // console.log(ringToDataPoints(xData))

  //   });
  
  //   oboe(`${host}/respeck/stream/${respeckMac}`).node("!", (v) => {
  //     // console.log(v)
  //     const [x, y, z] = v;
  //     console.log(xData.toArray())
  //     xData.push(x);
  //     yData.push(y);
  //     zData.push(z);

  //     setXGraphData(ringToDataPoints(xData))
  //     setYGraphData(ringToDataPoints(yData))
  //     setZGraphData(ringToDataPoints(zData))

  //   });
  // useEffect(() => setSeq(s => s + 1), [seq])

  return (
    <div>
      {/* <ShowcaseButton
        onClick={() => this.setState({ useCanvas: !useCanvas })}
        buttonContent={content}
      /> */}
      <XYPlot width={600} height={300}>
        <HorizontalGridLines />
        <VerticalGridLines />
        <XAxis />
        <YAxis />
        {/* <ChartLabel
          text="X Axis"
          className="alt-x-label"
          includeMargin={false}
          xPercent={0.025}
          yPercent={1.01}
        />

        <ChartLabel
          text="Y Axis"
          className="alt-y-label"
          includeMargin={false}
          xPercent={0.06}
          yPercent={0.06}
          style={{
            transform: "rotate(-90)",
            textAnchor: "end",
          }}
        /> */}
        <LineSeries
          className="series-x"
          style={{
            fill: "transparent",
          }}
          curve={"curveMonotoneX"}
          data={xGraphData}
        />
        <LineSeries
          className="series-y"
          style={{
            fill: "transparent",
          }}
          curve={"curveMonotoneX"}
          data={yGraphData}
        />
        <LineSeries
          className="series-z"
          style={{
            fill: "transparent",
          }}
          data={zGraphData}
        />
      </XYPlot>
    </div>
  );
};
