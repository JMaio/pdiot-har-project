// @ts-nocheck
import React, { useEffect, useMemo, useRef, useState } from "react";
import { DefaultApi } from "../openapiClient";
import { CanvasJSChart } from "canvasjs-react-charts";

export const PdiotChart = (props: any): JSX.Element => {
  const api = useMemo(() => new DefaultApi(), []);
  const [x, setX] = useState([]);
  const [y, setY] = useState([]);
  const [z, setZ] = useState([]);

  const chart = useRef();
  const [seq, setSeq] = useState(0);

  // const respeckMac = "E9:01:E8:22:92:C8".replaceAll(':', '-')
  // const respeckMac = "E9-01-E8-22-92-C8"; //.replaceAll(':', '-')
  const respeckMac = "C0-FF-EE"; //.replaceAll(':', '-')

  const options = {
    zoomEnabled: false,
    theme: "light2",
    title: {
      text: "Respeck data",
    },
    // axisX: {
    //   title: "time",
    // },
    // axisY: {
    //   suffix: " g",
    // },
    toolTip: {
      shared: true,
    },
    legend: {
      cursor: "pointer",
      verticalAlign: "top",
      fontSize: 18,
      fontColor: "dimGrey",
      // itemclick : this.toggleDataSeries
    },
    data: [
      {
        type: "line",
        // xValueFormatString: "#0",
        // yValueFormatString: "#.##0 g",
        showInLegend: true,
        name: "x",
        dataPoints: x,
      },
      {
        type: "line",
        // xValueFormatString: "#0",
        // yValueFormatString: "#.##0 g",
        showInLegend: true,
        name: "y",
        dataPoints: y,
      },
      {
        type: "line",
        // xValueFormatString: "#0",
        // yValueFormatString: "#.##0 g",
        showInLegend: true,
        name: "z",
        dataPoints: z,
      },
    ],
  };

  // https://upmostly.com/tutorials/setinterval-in-react-components-using-hooks
  useEffect(() => {
    const timer = setInterval(() => {
      // const data = api.getFullData().then(
      //     (v) => {
      //         console.log(v)
      //     },
      //     (error) => {
      //         console.log(error)
      //     })
      const data = api
        .getRespeckData({
          respeckMac: respeckMac,
        })
        .then(
          (r: RespeckData) => {
            // console.log(r.respeckData);
            const xSeq = []
            const ySeq = []
            const zSeq = []
            var s = 0
            r.respeckData.forEach(([x, y, z]) => {
              xSeq.push({ x: seq+s, y: x })
              ySeq.push({ x: seq+s, y: y })
              zSeq.push({ x: seq+s, y: z })
              s++;
              // console.log([x,y,z])
            })
            // console.log(seq+s)
            setSeq((old) => old+s)

            setX((p) => [...p, ...xSeq]);
            setY((p) => [...p, ...ySeq]);
            setZ((p) => [...p, ...zSeq]);

            // v.respeckData.forEach((x, y, z) => {
            //   // setX((p) => [{ x: x, y: seq }, ...p]);
            //   // setY((p) => [{ x: y, y: seq }, ...p]);
            //   // setZ((p) => [{ x: z, y: seq }, ...p]);
            //   setSeq(seq+1)
            // })
            try {
              updateChart(chart.current);
            } catch (error) {
              console.log(error)
            }
          },
          (error) => {
            console.log(error);
          }
        );
    }, 2000);
    return () => clearInterval(timer);
  }, [api, respeckMac, chart, seq]);

  // useEffect(() => {
  //   api
  //     .getRespeckStreamedData({
  //       respeckMac: respeckMac,
  //     })
  //     .then(
  //       (r: any) => {
  //         console.log(r);
  //         console.log("got r ^^^")
  //         const xSeq = [];
  //         const ySeq = [];
  //         const zSeq = [];
  //         var s = 0;
  //         r.respeckData.forEach(([x, y, z]) => {
  //           xSeq.push({ x: seq + s, y: x });
  //           ySeq.push({ x: seq + s, y: y });
  //           zSeq.push({ x: seq + s, y: z });
  //           s++;
  //         });
  //         setSeq((old) => old + s);

  //         setX((p) => [...p, ...xSeq]);
  //         setY((p) => [...p, ...ySeq]);
  //         setZ((p) => [...p, ...zSeq]);

  //         try {
  //           updateChart(chart.current);
  //         } catch (error) {
  //           console.log(error);
  //         }
  //       },
  //       (error) => {
  //         console.log(error);
  //       }
  //     );
  // }, [api, seq, x, y, z]);

  // useEffect(() => {
  //   // const updateChart = (c) => {
  //     try {
  //       const c = chart.current
  //       c.options.data[0].legendText = "x: " + x[0].y;
  //       c.options.data[1].legendText = "y: " + y[0].y;
  //       c.options.data[2].legendText = "z: " + z[0].y;
  //       c.render();
  //     } catch (error) {
  //       console.log(error)
  //     }
  //   // };
  // }, [x, y, z])

  // useEffect(() => {
  //   console.log(x)
  // }, [x])

  return (
    <div>
      <CanvasJSChart onRef={(ref) => (chart.current = ref)} options={options} />
    </div>
  );
};
