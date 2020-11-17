// @ts-nocheck
import React, { useEffect, useMemo, useRef, useState } from "react";
import { DefaultApi } from "../openapiClient";
import { CanvasJSChart } from "canvasjs-react-charts";
import oboe from "oboe";
import {
  ChartContainer,
  ChartRow,
  YAxis,
  Charts,
  LineChart
} from "react-timeseries-charts";
import Ring from "ringjs";
import { TimeRange, TimeSeries } from "pondjs";

const ws = 200


export const PdiotChartNew = (props: any): JSX.Element => {
  // const api = useMemo(() => new DefaultApi(), []);
  const xData = new Ring(ws);
  const yData = new Ring(ws);
  const zData = new Ring(ws);
  // const events = new Ring(200),
  const series = new TimeSeries({
    name: "Respeck",
    columns: ["x"],
    xData
  });
  // const timeRange = new TimeRange(0, ws)

  // const chart = useRef();
  // const [seq, setSeq] = useState(0);

  // const respeckMac = "E9:01:E8:22:92:C8".replaceAll(':', '-')
  // const respeckMac = "E9-01-E8-22-92-C8"; //.replaceAll(':', '-')
  const respeckMac = "C0-FF-EE"; //.replaceAll(':', '-')
  const host = "http://localhost:5000/api/v1";


  useEffect(() => {
    var s = 0;
    // const xData = []
    // const yData = []
    // const zData = []
    oboe(`${host}/respeck/stream/${respeckMac}`).node("!", (v) => {
      // console.log(v)
      const [x, y, z] = v;

      xData.push(new Event(s, x))
      yData.push(new Event(s, y))
      zData.push(new Event(s, z))      
      // setXData((p) => [...p, { x: s, y: x }]);
      // setYData((p) => [...p, { x: s, y: y }]);
      // setZData((p) => [...p, { x: s, y: z }]);
      // xData.push({ x: s, y: x })
      // yData.push({ x: s, y: y })
      // zData.push({ x: s, y: z })

      s++;
    });
  });

  return (
    <ChartContainer 
    timeRange={series.range()}
    >
      <ChartRow height="150">
        <YAxis id="y" label="Value" 
        // min={0} max={1500} 
        type="linear" />
        <Charts>
          {/* <BarChart axis="y" series={avgSeries} columns={["value"]} />
          <BarChart axis="y" series={maxSeries} columns={["value"]} /> */}
          <LineChart
            axis="t-axis"
            // style={style}
            spacing={1}
            // columns={["median"]}
            interpolation="curveBasis"
            series={xData}
          />
          {/* < axis="y" series={rawSeries} /> */}
        </Charts>
      </ChartRow>
    </ChartContainer>
  );
};
