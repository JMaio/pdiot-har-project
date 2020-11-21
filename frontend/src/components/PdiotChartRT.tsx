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
  LineChart,
} from "react-timeseries-charts";
import Ring from "ringjs";
import { TimeRange, TimeSeries } from "pondjs";
import RTChart from "react-rt-chart";

const ws = 200;

export const PdiotChartRT = (props: any): JSX.Element => {
  // const api = useMemo(() => new DefaultApi(), []);
//   const xData = new Ring(ws);
//   const yData = new Ring(ws);
//   const zData = new Ring(ws);
  // const events = new Ring(200),
  //   const series = new TimeSeries({
  //     name: "Respeck",
  //     columns: ["x"],
  //     xData
  //   });

  const [newData, setNewData] = useState([]);
  // const timeRange = new TimeRange(0, ws)

  // const chart = useRef();
  const [seq, setSeq] = useState(0);

  // const respeckMac = "E9:01:E8:22:92:C8".replaceAll(':', '-')
  // const respeckMac = "E9-01-E8-22-92-C8"; //.replaceAll(':', '-')
  const respeckMac = "C0-FF-EE"; //.replaceAll(':', '-')
  const host = "http://localhost:5000/api/v1";

  useEffect(() => {
    var s = 0;
    // const xData = []
    // const yData = []
    // const zData = []
    const lineArr = []
    
    oboe(`${host}/respeck/stream/${respeckMac}`).node("!", (v) => {
      // console.log(v)
      const [x, y, z] = v;
      lineArr.push({
        time: s,
        x: x,
        y: y,
        z: z,
      });
      s++;

      setNewData({
        date: 10000,
        acc_x: seq+x,
        // y: y,
        // z: z,
      });
      setSeq(s => s+1)

      //   xData.push(new Event(s, x))
      //   yData.push(new Event(s, y))
      //   zData.push(new Event(s, z))
      // setXData((p) => [...p, { x: s, y: x }]);
      // setYData((p) => [...p, { x: s, y: y }]);
      // setZData((p) => [...p, { x: s, y: z }]);
      // xData.push({ x: s, y: x })
      // yData.push({ x: s, y: y })
      // zData.push({ x: s, y: z })

    });
  });

  // useEffect(() => setSeq(s => s + 1), [seq])

  return <RTChart fields={["acc_x"]} data={newData} />;
};
