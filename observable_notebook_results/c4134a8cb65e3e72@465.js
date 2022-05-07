// https://observablehq.com/@fil/plot-regression@465
import define1 from "./fc3cb8c5fd68eb6f@552.js";

function _1(md){return(
md`# Plot: regression

This plugin for Observable Plot wraps Harry Stevens’ [d3.regression](https://observablehq.com/@harrystevens/introducing-d3-regression) module in a [Plot transform](https://observablehq.com/@observablehq/plot-transforms). As we can see in the example below, the regression between culmen length and culmen depth shows a positive correlation within each species of penguins (colored lines). However, the overall correlation if we consider the three species together is negative (dotted line). A typical case of [Simpson’s paradox](https://en.wikipedia.org/wiki/Simpson%27s_paradox)…`
)}

function _2(Plot,penguins){return(
Plot.plot({
  grid: true,
  marks: [
    Plot.dot(penguins, {
      x: "culmen_length_mm",
      y: "culmen_depth_mm",
      fill: "species"
    }),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        strokeDasharray: [1.5, 4],
        strokeWidth: 1.5
      })
    ),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        stroke: "species"
      })
    )
  ]
})
)}

function _3(md){return(
md`---
The **type** parameter allows you to selected another type of regression (see [d3.regression](https://observablehq.com/@harrystevens/introducing-d3-regression) for the complete documentation), possibly setting a secondary parameter (**bandwidth** for LOESS, and **order** for polynomial regression).`
)}

function _param(type,Inputs,html){return(
type === "LOESS"
  ? Inputs.range([0, 1], { label: "bandwidth (LOESS)" })
  : type === "Poly"
  ? Inputs.range([1, 5], { label: "poly order", value: 3, step: 1 })
  : Object.assign(html`—`, { value: null })
)}

function _type(Inputs){return(
Inputs.select(["Linear", "Quad", "Poly", "Log", "Exp", "LOESS"], {
  label: "type"
})
)}

function _6(Plot,penguins,type,param){return(
Plot.plot({
  grid: true,
  marks: [
    Plot.dot(penguins, {
      x: "culmen_length_mm",
      y: "culmen_depth_mm",
      fill: "species"
    }),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        strokeDasharray: [1.5, 4],
        strokeWidth: 1.5,
        type,
        bandwidth: param,
        order: param
      })
    ),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        stroke: "species",
        type,
        bandwidth: param,
        order: param
      })
    )
  ]
})
)}

function _7(md){return(
md`And faceting works as expected…`
)}

function _8(Plot,penguins,width){return(
Plot.plot({
  grid: true,
  facet: {
    data: penguins,
    x: "sex"
  },
  marks: [
    Plot.frame(),
    Plot.dot(penguins, {
      x: "culmen_length_mm",
      y: "culmen_depth_mm",
      fill: "species"
    }),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        strokeDasharray: [1.5, 4],
        strokeWidth: 1.5
      })
    ),
    Plot.line(
      penguins,
      Plot.regression({
        x: "culmen_length_mm",
        y: "culmen_depth_mm",
        stroke: "species"
      })
    )
  ],
  width
})
)}

function _9(md){return(
md`---
_todo_

In a not-too-distant future I would like to understand how the standard error / confidence interval bands are computed, and add them to the plot. Help welcome! Reference Plot issue [#168](https://github.com/observablehq/plot/issues/168).`
)}

function _10(md){return(
md`---
_code_`
)}

function _addRegression(reg,d3){return(
function addRegression(Plot) {
  Plot.regression = function ({ x, y, type, bandwidth, order, ...options }) {
    type = String(type).toLowerCase();
    const regressor =
      type === "quad"
        ? reg.regressionQuad()
        : type === "poly"
        ? reg.regressionPoly()
        : type === "pow"
        ? reg.regressionPow()
        : type === "exp"
        ? reg.regressionExp()
        : type === "log"
        ? reg.regressionLog()
        : type === "loess"
        ? reg.regressionLoess()
        : reg.regressionLinear();
    if (bandwidth && regressor.bandwidth) regressor.bandwidth(bandwidth);
    if (order && regressor.order) regressor.order(order);

    const z = options.z || options.stroke; // maybeZ
    return Plot.transform(options, function (data, facets) {
      const X = Plot.valueof(data, x);
      const Y = Plot.valueof(data, y);
      const Z = Plot.valueof(data, z);
      regressor.x((i) => X[i]).y((i) => Y[i]);

      const regFacets = [];
      const points = [];
      for (const facet of facets) {
        const regFacet = [];
        for (const I of Z ? d3.group(facet, (i) => Z[i]).values() : [facet]) {
          const reg = regressor(I);
          for (const d of reg) {
            const j = points.push(d) - 1;
            if (z) d[z] = Z[I[0]];
            regFacet.push(j);
          }
        }
        regFacets.push(regFacet);
      }
      return { data: points, facets: regFacets };
    });
  };
  return Plot;
}
)}

function _penguins(FileAttachment){return(
FileAttachment("penguins.csv").csv({typed: true})
)}

function _reg(require){return(
require("d3-regression@1")
)}

function _Plot(addRegression,Plot_){return(
addRegression(Plot_)
)}

export default function define(runtime, observer) {
  const main = runtime.module();
  function toString() { return this.url; }
  const fileAttachments = new Map([
    ["penguins.csv", {url: new URL("./files/715db1223e067f00500780077febc6cebbdd90c151d3d78317c802732252052ab0e367039872ab9c77d6ef99e5f55a0724b35ddc898a1c99cb14c31a379af80a", import.meta.url), mimeType: "text/csv", toString}]
  ]);
  main.builtin("FileAttachment", runtime.fileAttachments(name => fileAttachments.get(name)));
  main.variable(observer()).define(["md"], _1);
  main.variable(observer()).define(["Plot","penguins"], _2);
  main.variable(observer()).define(["md"], _3);
  main.variable(observer("viewof param")).define("viewof param", ["type","Inputs","html"], _param);
  main.variable(observer("param")).define("param", ["Generators", "viewof param"], (G, _) => G.input(_));
  main.variable(observer("viewof type")).define("viewof type", ["Inputs"], _type);
  main.variable(observer("type")).define("type", ["Generators", "viewof type"], (G, _) => G.input(_));
  main.variable(observer()).define(["Plot","penguins","type","param"], _6);
  main.variable(observer()).define(["md"], _7);
  main.variable(observer()).define(["Plot","penguins","width"], _8);
  main.variable(observer()).define(["md"], _9);
  main.variable(observer()).define(["md"], _10);
  main.variable(observer("addRegression")).define("addRegression", ["reg","d3"], _addRegression);
  main.variable(observer("penguins")).define("penguins", ["FileAttachment"], _penguins);
  main.variable(observer("reg")).define("reg", ["require"], _reg);
  main.variable(observer("Plot")).define("Plot", ["addRegression","Plot_"], _Plot);
  const child1 = runtime.module(define1);
  main.import("Plot", "Plot_", child1);
  return main;
}
