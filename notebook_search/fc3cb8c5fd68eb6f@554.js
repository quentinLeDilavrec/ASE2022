// https://observablehq.com/@fil/plot-splom@554
import define1 from "./32eeadb67cb4cbcb@1472.js";

function _1(md){return(
md`# Plot scatter plot matrix

The scatter plot matrix (alias SPLOM) is a symmetric grid where each row represents a dimension of the dataset. It allows to see distributions and correlations.`
)}

function _2(md){return(
md`_Plot exposes Plot.transform and Plot.channel, allowing the composition of elaborate transforms. In this case, the custom **normalizeMaxY** transform is applied after binning and stacking, and reduces the highest bar of each facet to the maximum height = 0.8._`
)}

function _chart(Plot,elements,dimensions,normalizeMaxY){return(
Plot.plot({
  facet: {
    data: elements,
    x: "dimx",
    y: "dimy"
  },
  fx: {
    domain: dimensions,
    axis: null
  },
  fy: {
    domain: dimensions,
    tick: 20,
    axis: null
  },
  x: {
    axis: null,
    domain: [0, 1]
  },
  y: {
    axis: null,
    domain: [0, 1]
  },
  marks: [
    // grey grid
    Plot.tickX(elements, {
      x: "x",
      filter: (d) => d.type === "tick",
      strokeWidth: 0.5,
      stroke: "#ccc"
    }),
    Plot.tickY(elements, {
      y: "y",
      filter: (d) => d.type === "tick" && d.dimx !== d.dimy,
      strokeWidth: 0.5,
      stroke: "#ccc"
    }),

    // black frame
    Plot.frame(),

    // colored data dots (penguins!)
    Plot.dot(elements, {
      filter: (d) => d.type === "dot" && d.dimx !== d.dimy,
      x: "x",
      y: "y",
      fill: "species",
      r: 2,
      fillOpacity: 0.7
    }),

    // distributions
    Plot.rectY(
      elements,
      normalizeMaxY(
        Plot.stackY(
          Plot.binX(
            { y: "count" },
            {
              filter: (d) => d.type === "dot" && d.dimx === d.dimy,
              x: "x",
              fill: "species",
              maxHeight: 0.8
            }
          )
        )
      )
    ),

    // tick marks, on the X axis
    Plot.text(elements, {
      filter: (d) =>
        d.type === "tick" && d.dimy === dimensions[dimensions.length - 1],
      x: "x",
      y: () => 0,
      text: "value",
      dy: 12
    }),

    // tick marks, on the Y axis
    Plot.text(elements, {
      filter: (d) => d.type === "tick" && d.dimx === dimensions[0],
      y: "y",
      x: () => 0,
      text: "value",
      textAnchor: "end",
      dx: -4
    }),

    // dimension labels // text: label filters implicitely
    Plot.text(elements, {
      filter: (d) => d.type === "label",
      y: () => 0.94,
      x: () => 0.03,
      text: "label",
      textAnchor: "start",
      fontWeight: "bold"
    })
  ],
  marginLeft: 50,
  marginRight: 30,
  marginTop: 10,
  marginBottom: 30,
  width: 780,
  height: 700
})
)}

function _Plot(require){return(
require("@observablehq/plot@0.5")
)}

function _normalizeMaxY(Plot,d3){return(
function normalizeMaxY({ y, maxHeight = 1, ...options }) {
  const [H1, setH1] = Plot.column(y);
  const [H2, setH2] = Plot.column(y);
  options = Plot.transform(options, function (data, facets) {
    const Y1 = options.y1.transform();
    const Y2 = options.y2.transform();
    const H1 = new Array(data.length);
    const H2 = new Array(data.length);
    setH1(H1);
    setH2(H2);
    for (const index of facets) {
      const M = maxHeight / (d3.max(index, (i) => Y2[i]) || 1);
      for (const i of index) {
        H1[i] = Y1[i] * M;
        H2[i] = Y2[i] * M;
      }
    }
    return { data, facets };
  });
  return { ...options, y1: H1, y2: H2 };
}
)}

function _dimensions(){return(
[
  "bill_length",
  "bill_depth",
  "flipper_length",
  "body_mass",
  "island"
]
)}

function _reversed(){return(
[]
)}

function _ordinal(dimensions,data){return(
dimensions.filter(
  (dim) => typeof data.find((d) => d[dim] != null)[dim] !== "number"
)
)}

function _scales(d3,data,dimensions,ordinal,reversed){return(
d3.rollup(
  data.flatMap((d) =>
    dimensions.map((dimension) => ({ dimension, value: d[dimension] }))
  ),
  (v) =>
    (ordinal.includes(v[0].dimension)
      ? d3
          .scalePoint()
          .domain([...new Set(v.map((d) => d.value))].sort())
          .padding(0.6)
      : d3.scaleLinear().domain(d3.extent(v, (d) => d.value))
    ).range(reversed.includes(v[0].dimension) ? [0.95, 0.05] : [0.05, 0.95]),
  (d) => d.dimension
)
)}

function _scaled(data,dimensions,scales){return(
data.map((d) => ({
  ...d,
  ...Object.fromEntries(
    dimensions.map((dimension) => [
      `_${dimension}`,
      scales.get(dimension)(d[dimension])
    ])
  )
}))
)}

function _dots(d3,scales,scaled){return(
d3.cross(scales.keys(), scales.keys()).flatMap(([dimx, dimy]) =>
  scaled.map((d) => ({
    ...d,
    dimx,
    dimy,
    x: d[`_${dimx}`],
    y: d[`_${dimy}`],
    type: "dot"
  }))
)
)}

function _elements(dots,ticksX,ticksY,labels){return(
[].concat(dots).concat(ticksX).concat(ticksY).concat(labels)
)}

function _ticksX(d3,scales,ordinal){return(
d3.cross(scales.keys(), scales.keys()).flatMap(([dimx, dimy]) => {
  const s = scales.get(dimx);
  const ticks = ordinal.includes(dimx) ? s.domain() : s.ticks(5);
  return ticks.map((value) => ({
    dimx,
    dimy,
    value,
    x: scales.get(dimx)(value),
    type: "tick"
  }));
})
)}

function _ticksY(d3,scales,ordinal){return(
d3.cross(scales.keys(), scales.keys()).flatMap(([dimx, dimy]) => {
  const s = scales.get(dimy);
  const ticks = ordinal.includes(dimy) ? s.domain() : s.ticks(5);
  return ticks.map((value) => ({
    dimx,
    dimy,
    value,
    y: scales.get(dimy)(value),
    type: "tick"
  }));
})
)}

function _labels(scales){return(
Array.from(scales.keys(), (dim) => ({
  dimx: dim,
  dimy: dim,
  label: dim,
  type: "label"
}))
)}

export default function define(runtime, observer) {
  const main = runtime.module();
  main.variable(observer()).define(["md"], _1);
  main.variable(observer()).define(["md"], _2);
  main.variable(observer("chart")).define("chart", ["Plot","elements","dimensions","normalizeMaxY"], _chart);
  main.variable(observer("Plot")).define("Plot", ["require"], _Plot);
  main.variable(observer("normalizeMaxY")).define("normalizeMaxY", ["Plot","d3"], _normalizeMaxY);
  const child1 = runtime.module(define1);
  main.import("data", child1);
  main.variable(observer("dimensions")).define("dimensions", _dimensions);
  main.variable(observer("reversed")).define("reversed", _reversed);
  main.variable(observer("ordinal")).define("ordinal", ["dimensions","data"], _ordinal);
  main.variable(observer("scales")).define("scales", ["d3","data","dimensions","ordinal","reversed"], _scales);
  main.variable(observer("scaled")).define("scaled", ["data","dimensions","scales"], _scaled);
  main.variable(observer("dots")).define("dots", ["d3","scales","scaled"], _dots);
  main.variable(observer("elements")).define("elements", ["dots","ticksX","ticksY","labels"], _elements);
  main.variable(observer("ticksX")).define("ticksX", ["d3","scales","ordinal"], _ticksX);
  main.variable(observer("ticksY")).define("ticksY", ["d3","scales","ordinal"], _ticksY);
  main.variable(observer("labels")).define("labels", ["scales"], _labels);
  return main;
}
