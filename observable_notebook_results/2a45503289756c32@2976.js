import define1 from "./26670360aa6f343b@202.js";
import define2 from "./c4134a8cb65e3e72@465.js";

function _1(md){return(
md`# Timewise scaling of AST construction`
)}

function _2(md){return(
md`## Perfs scaling

RQ: Does our approach scales better than the state of the art both in construction time and memory footprint ?

1) (# of new git objects, # of files in commit, # of new hyperAST nodes) over # of commits
  - show proportionality of trad approach with # of files in commit
  - show propotionality of our approach with # of new element

2) construction time of AST (spoon vs HyperAST) per commit (over computed commits)

3) construction time of AST (spoon vs HyperAST) over computed commits

4) memory footprint of AST (spoon vs HyperAST) over computed commits`
)}

function _3(html){return(
html`
<style type="text/css">

  #svg {
    background-color: black;
  }

  .rect {
    fill: white;
  }
  text {
    font-size: 14px;
  }

</style>
`
)}

function _4(perfs,projects_by_size,Plot,decls_factor,max_matched_decls,reg,d3,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = perfs.filter(x=>projects_by_size.includes(x.project)).map(d=> ({...d, type: (f_type[d.type]) }));
  console.log(d_plus)
  const dimension = "construction_memory_fooprint";
  const lines = 
    Plot.line(d_plus, {
      x:'no',
      y: dimension, 
      stroke: 'type',
      domain: [-1, 1200],
      facet: "include",
      //clip: true,
      filter: d => d.no<1000,// && d[dimension]>.5,
      strokeWidth: decls_factor ? d => d.matched_decl_count/max_matched_decls.get(d.project)*12 : 2,
      //sort: (a,b) => projects_scale(b.project)-projects_scale(a.project),
    });
  const r = (X,Y) => reg.regressionLinear().x((i) => X[i]).y((i) => Y[i]);
  const a = (d) => {
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type)
    const s = r(v.map(d=>d.no),v.map(d=>d[dimension]))(v.map((x,i)=>i));
    // let z = s.predict(0);
    // let quant_up = d3.quantile(v.map(d=>d[dimension]), 0.975);
    // let quant_down = d3.quantile(v.map(d=>d[dimension]), 0.025);
    // let z = v.map(d=>d[dimension]).filter(x=>quant_down<x&&x<quant_up).reduce((x,y)=>x+y*50,0)/v.length
    // let z = v.map(d=>d[dimension]).reduce((x,y)=>x+y*50,0)/(50*v.length)
    let z;
    if (d.type==='spoon') {
      // we only sampled every 50 commits because the baseline tool cost so much more to run
      z = v.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)
    } else {
      z = v.map(d=>d[dimension]).reduce((x,y)=>x+y,0)
    }
    return d3.format('.2~s')(z*1000*1000)+'b'
    // if (z > 1000) {
    //   return Math.trunc(z/1000*100)/100 + 'Tb'
    // } else {
    //   return Math.trunc(z*100)/100 + 'Gb'
    // }
  };
  // spoon / hyperAST
  const factor = (d) => {
    // we only sampled every 50 commits because the baseline tool cost so much more to run
    const v_bl = d_plus.filter(x=>x.project===d.project && x.type==='spoon')
    const v_ev = d_plus.filter(x=>x.project===d.project && x.type==='hyperAST')
    let z = v_bl.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)/v_ev.map(d=>d[dimension]).reduce((x,y)=>x+y,0)
    return 'X ' + d3.format('d')(z)
  };
  //100 -  (hyperast * 100 / spoon)
  const gain = (d) => {
    // we only sampled every 50 commits because the baseline tool cost so much more to run
    const v_bl = d_plus.filter(x=>x.project===d.project && x.type==='spoon')
    const v_ev = d_plus.filter(x=>x.project===d.project && x.type==='hyperAST')
    const bl = v_bl.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)
    const ev = v_ev.map(d=>d[dimension]).reduce((x,y)=>x+y,0)
    let z = 1.-ev/bl
    return d3.format('.2%')(z)
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -180,
        z:'project',
        text: d => a(d),
        //facet: "exclude",
        // fill: "currentColor",
        fill: "type",
        stroke: "white",
        facet: "include",
        fontSize:12,
        filter: d=>d.type==='spoon',
      })),
    //boxPlotY(d_plus)
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -150,
        z:'project',
        text: d => a(d),
        fill: "type",
        stroke: "white",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:12,
      })),
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -120,
        z:'project',
        text: d => factor(d),
        stroke: "white",
        fill: "black",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:12,
      })),
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -100,
        z:'project',
        text: d => gain(d),
        stroke: "white",
        fill: "black",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:12,
      })),
    // Plot.line(
    //   d_plus,
    //   Plot.regression({
    //     x:'no',
    //     y: dimension, 
    //     stroke : 'type',
    //     strokeDasharray: [1.5, 4],
    //     strokeWidth: 10.5,
    //     type: "loess",
    //   })
    // ),
  ],
  facet: {
    data: d_plus,
    x: 'project',
    label: null,
  },
  x: {
    label:"Commits",
    domain: [0,1100],
    ticks: 1,
    tickRotate:-10,
    labelAnchor: 'right',
    // scale.fontVariant
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
    tickRotate: -15,
    labelAnchor: 'left',
  },
  y: {
    label:'Memory fooprint',
    grid: true,
    type: scale,
    tickFormat: d=> d3.format('.2~s')(d*1000*1000)+'b'
  },
  grid: true,
  marginBottom: 50,
  marginRight: 30,
  width: 1324,
  height: 580,
  fontSize:19,
})
}


function _5(perfs,projects_by_size,Plot,decls_factor,max_matched_decls,reg,d3,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = perfs.filter(x=>projects_by_size.includes(x.project)).map(d=> ({...d, type: (f_type[d.type]) }));
  const dimension = "construction_time";
  const lines = 
    Plot.line(d_plus, {
      x:'no',
      y: dimension, 
      stroke: 'type',
      //domain: [0, 10],
      facet: "include",
      //clip: true,
      filter: d => d.no<1000,// && d[dimension]>.5,
      strokeWidth: decls_factor ? d => d.matched_decl_count/max_matched_decls.get(d.project)*12 : 2,
      //sort: (a,b) => projects_scale(b.project)-projects_scale(a.project),
    });
  const r = (X,Y) => reg.regressionLinear().x((i) => X[i]).y((i) => Y[i]);
  const a = (d) => {
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type)
    const s = r(v.map(d=>d.no),v.map(d=>d[dimension]))(v.map((x,i)=>i));
    // let z = s.predict(0);
    // let quant_up = d3.quantile(v.map(d=>d[dimension]), 0.975);
    // let quant_down = d3.quantile(v.map(d=>d[dimension]), 0.025);
    // let z = v.map(d=>d[dimension]).filter(x=>quant_down<x&&x<quant_up).reduce((x,y)=>x+y*50,0)/v.length
    // let z = v.map(d=>d[dimension]).reduce((x,y)=>x+y*50,0)/(50*v.length)
    let z;
    if (d.type==='spoon') {
      // we only sampled every 50 commits because the baseline tool cost so much more to run
      let quant_up = d3.quantile(v.map(d=>d[dimension]), 0.975);
      z = v.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)/1000
    } else {
      z = v.map(d=>d[dimension]).reduce((x,y)=>x+y,0)/1000
    }
    let t = z*1000
    // return Math.trunc(t/60/60) + 'h' + Math.trunc(t/60-h*60)
    return Math.trunc(t/60/60) + 'h' + d3.timeFormat('%M')(new Date(t*1000))
    // return d3.timeFormat('%X')(new Date(t*1000))
  };
  // spoon / hyperAST
  const factor = (d) => {
    // we only sampled every 50 commits because the baseline tool cost so much more to run
    const v_bl = d_plus.filter(x=>x.project===d.project && x.type==='spoon')
    const v_ev = d_plus.filter(x=>x.project===d.project && x.type==='hyperAST')
    let z = v_bl.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)/v_ev.map(d=>d[dimension]).reduce((x,y)=>x+y,0)
    return 'X ' + d3.format('d')(z)
  };
  //100 -  (hyperast * 100 / spoon)
  const gain = (d) => {
    // we only sampled every 50 commits because the baseline tool cost so much more to run
    const v_bl = d_plus.filter(x=>x.project===d.project && x.type==='spoon')
    const v_ev = d_plus.filter(x=>x.project===d.project && x.type==='hyperAST')
    const bl = v_bl.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)
    const ev = v_ev.map(d=>d[dimension]).reduce((x,y)=>x+y,0)
    let z = 1.-ev/bl
    return d3.format('.2%')(z)
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 25,
        dy: -160,
        z:'project',
        text: d => a(d),
        //facet: "exclude",
        // fill: "currentColor",
        fill: "type",
        stroke: "white",
        facet: "include",
        filter: d=>d.type==='spoon',
        fontSize:15,
      })),
    //boxPlotY(d_plus)
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 25,
        dy: -120,
        z:'project',
        text: d => a(d),
        fill: "type",
        stroke: "white",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:15,
      })),
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -80,
        z:'project',
        text: d => factor(d),
        stroke: "white",
        fill: "black",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:15,
      })),
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 29,
        dy: -60,
        z:'project',
        text: d => gain(d),
        stroke: "white",
        fill: "black",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        fontSize:15,
      })),
  ],
  facet: {
    data: d_plus,
    x: 'project',
    label: null,
  },
  x: {
    label:"commits",
    domain: [0,1100],
    ticks: 1,
    tickRotate:-10,
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
    tickRotate: -11,
    labelAnchor: 'left',
    //tickRotate:10,
    //tickPadding: d => {debugger; return projects_by_size.indexOf(d.project)%2 === 0 ? 10 : 30},
  },
  y: {
    label:'Time (minutes:seconds)',
    grid: true,
    type: scale,
    tickFormat: d=> d3.timeFormat('%M:%S')(new Date(d * 1000))
  },
  grid: true,
  marginBottom: 50,
  marginRight: 30,
  width: 1324,
  height: 580,
  fontSize:19,
})
}


function _6(data,Plot,decls_factor,max_matched_decls,reg,d3,projects_by_size,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = data.map(d=> ({...d, type: (f_type[d.type]) }));
  const dimension = "search_time";
  const lines = 
    Plot.line(d_plus, {
      x:'no',
      y: dimension, 
      stroke: 'type',
      //domain: [0, 10],
      facet: "include",
      clip: true,
      filter: d => d.no<1000,// && d[dimension]>.5,
      strokeWidth: decls_factor ? d => d.matched_decl_count/max_matched_decls.get(d.project)*12 : 2,
      //sort: (a,b) => projects_scale(b.project)-projects_scale(a.project),
    });
  const r = (X,Y) => reg.regressionLinear().x((i) => X[i]).y((i) => Y[i]);
  const a = (d) => {
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type)
    const s = r(v.map(d=>d.no),v.map(d=>d[dimension]))(v.map((x,i)=>i));
    // let z = s.predict(0);
    // let quant_up = d3.quantile(v.map(d=>d[dimension]), 0.975);
    // let quant_down = d3.quantile(v.map(d=>d[dimension]), 0.025);
    // let z = v.map(d=>d[dimension]).filter(x=>quant_down<x&&x<quant_up).reduce((x,y)=>x+y*50,0)/v.length
    // let z = v.map(d=>d[dimension]).reduce((x,y)=>x+y*50,0)/(50*v.length)
    let z;
    z = v.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)/1000
    let t = z*1000
    // return Math.trunc(t/60/60) + 'h' + Math.trunc(t/60-h*60)
    // return Math.trunc(t/60/60) + 'h' + d3.timeFormat('%M')(new Date(t*1000))
    return Math.trunc(t/60/1000) + 'm' + d3.timeFormat('%S')(new Date(t))
    // return d3.timeFormat('%X')(new Date(t*1000))
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 28,
        dy: -90,
        z:'project',
        text: d => a(d),
        //facet: "exclude",
        // fill: "currentColor",
        fill: "type",
        stroke: "white",
        facet: "include",
        fontSize: 16,
        filter: d=>d.type==='spoon',
      })),
    //boxPlotY(d_plus)
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 28,
        dy:-42,
        z:'project',
        text: d => a(d),
        fill: "type",
        stroke: "white",
        facet: "include",
        fontSize: 16,
        filter: d=>d.type==='hyperAST',
      })),
  ],
  facet: {
    data: d_plus,
    x: 'project',
    label: null,
  },
  x: {
    label:"commits",
    domain: [0,1100],
    ticks: 1,
    tickRotate:-10,
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
    tickRotate: -11,
    labelAnchor: 'left',
    //tickRotate:10,
    //tickPadding: d => {debugger; return projects_by_size.indexOf(d.project)%2 === 0 ? 10 : 30},
  },
  y: {
    label:'Time (minutes:seconds)',
    grid: true,
    type: scale,
    domain:[0,2200],
    tickFormat: d=> d3.timeFormat('%M:%S')(new Date(d * 1000))
  },
  grid: true,
  marginBottom: 50,
  marginRight: 30,
  width: 1324,
  height: 640,
  fontSize:19,
})
}


function _dimension(Inputs){return(
Inputs.select(['construction_time',
  'search_time',
  'construction_memory_fooprint',
  'with_search_memory_fooprint',
  // 'matching'
], { label: 'Dimension', value: 'search_time' })
)}

function _scale(Inputs){return(
Inputs.select(['linear','log'], { label: 'Scale', value: 'linear' })
)}

function _decls_factor(Inputs){return(
Inputs.toggle({ label: 'Factor in declarations', value: false })
)}

function _10(data,Plot,dimension,decls_factor,max_matched_decls,reg,projects_by_size,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = data.map(d=> ({...d, type: (f_type[d.type]) }))
  const lines = 
    Plot.line(d_plus, {
      x:'no',
      y: dimension, 
      stroke: 'type',
      //domain: [0, 10],
      facet: "include",
      clip: true,
      //filter: d => d.no<40,// && d[dimension]>.5,
      strokeWidth: decls_factor ? d => d.matched_decl_count/max_matched_decls.get(d.project)*12 : 2,
      //sort: (a,b) => projects_scale(b.project)-projects_scale(a.project),
    });
  const r = (X,Y) => reg.regressionLoess().x((i) => X[i]).y((i) => Y[i]);
  const a = (d) => {
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type)
    const s = r(v.map(d=>d.no),v.map(d=>d[dimension]))(v.map((x,i)=>i));
    // let z = s.predict(0);
    // let quant_up = d3.quantile(v.map(d=>d[dimension]), 0.975);
    // let quant_down = d3.quantile(v.map(d=>d[dimension]), 0.025);
    // let z = v.map(d=>d[dimension]).filter(x=>quant_down<x&&x<quant_up).reduce((x,y)=>x+y*50,0)/v.length
    // let z = v.map(d=>d[dimension]).reduce((x,y)=>x+y*50,0)/(50*v.length)
    console.log(s)
    let z = [...Array(v.length*50).keys()].map(i=>i).reduce((x,y)=>x+y,0)
    if (dimension === 'construction_memory_fooprint' || dimension === 'with_search_memory_fooprint') {
      return Math.trunc(z*100)/100 + 'Gb'
    }
    let t = z*1000
    const h = Math.trunc(t/60/60)
    return h + 'h' + Math.trunc(t/60-h*60)
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(d_plus, Plot.selectFirst({
        x: 0,//"no",
        dx: 25,
        dy: -150,
        z:'project',
        text: d => a(d),
        //facet: "exclude",
        // fill: "currentColor",
        fill: "type",
        stroke: "white",
        facet: "include",
        fontSize:12,
        filter: d=>d.type==='spoon',
      })),
    //boxPlotY(d_plus)
    Plot.text(d_plus, Plot.selectFirst({
        //text: (d) => labels.get(d.species).text
        x: 0,//"no",
        dx: 25,
        dy: -100,
        z:'project',
        //y: 100,//"previous",
        // text: d => d.type + '=' +  a(d),
        text: d => a(d),
        //facet: "exclude",
        // fill: "currentColor",
        fill: "type",
        stroke: "white",
        facet: "include",
        filter: d=>d.type==='hyperAST',
        //reverse: true,
        //fill: "#8a817c",
        // dy: "100.5em",
        //y: dimension,//100, 
        //stroke : 'type',
        // stroke: d => ({baseline:"spoon",evaluation:"hyperAST"}[d.type]),
        // facet: "include",
       // clip: true,
        //facet: 'include',
      })),
  ],
  facet: {
    data: d_plus,
    x: 'project',
    label: null,
  },
  x: {
    // reverse: true,
    //nice: true,
    label:"commits",
    domain: [0,1100],
    ticks: 1,
    tickRotate:-10,
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
  },
  y: {
    grid: true,
    //inset: 6,
    // domain: [.5,1.],
    type: scale,
  },
  grid: true,
  marginBottom: 45,
  marginRight: 30,
  width: 1324,
  height: 640,
  fontSize:19,
})
}


function _reg(require){return(
require("d3-regression@1")
)}

function _dimension_decls(Inputs){return(
Inputs.select(['matched_decl_count',
  'decl_count',
  'decl_count_rem'], { label: 'Dimension declarations' })
)}

function _13(Plot,data,dimension_decls,projects_scale,projects_by_size){return(
Plot.plot({
  marks: [
    Plot.line(data, {
      x:'no',
      y: dimension_decls, 
      stroke:'type',
      //domain: [0, 40],
      //facet: "include",
      // filter: d => d.type === 'baseline
      strokeWidth: 2,
      clip: true,
    }),
  ],
  facet: {
    data: data,
    x: 'project',
    sort: {
      x: (a,b) => projects_scale(b)-projects_scale(a),
      //sort: (a,b) => projects_scale(b)-projects_scale(a),
      domain: projects_by_size,//[...projects_by_size.slice(0,8)],
    },
  },
  x: {
    nice: true,
    domain: [0,1100],
    ticks: 1,
    tickRotate:-10,
    label:"commits",
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
  },
  y: {
    grid: true,
    //type: "log",
  },
  grid: true,
  marginBottom: 60,
  marginRight: 30,
  marginLeft: 50,
  width: 1324,
  height: 640
})
)}

function _15(Plot,data,dimension,scale){return(
Plot.plot({
  marks: [
    //Plot.ruleY([Math.floor(d3.min(data.map(d => d[dimension])))]),
    //rawDataY,
    Plot.boxY(data, {x:'type',y: dimension}),
    //boxPlotY(data)
  ],
  facet: {
    data: data,
    x: 'project',
    marginBottom: 40
  },
  x: {
    ticks: 3,
    tickFormat: d => d < 0 ? '' : d,
  },
  fx: {
    axis: 'bottom',
  },
  y: {
    grid: true,
    //inset: 6,
    //domain: [.9,1.],
    type: scale
  },
  marginBottom: 60,
  marginRight: 60,
  width: 1200,
  height: 640
})
)}

function _precision(d3,data){return(
d3.mean(data.filter(x=>x.type==='baseline').map(x=>x.matching).filter(x=>x))
)}

function _recall(d3,data){return(
d3.mean(data.filter(x=>x.type==='evaluation').map(x=>x.matching).filter(x=>x))
)}

function _18(Plot,data,max_matched_decls,d3,projects_by_size,scale)
{
  const lines = 
    Plot.line(data, {
      x:'no',
      y: 'matching',//d => d.matching < 0.5 ? undefined : d.matching , 
      //stroke: d => ({baseline:"spoon",evaluation:"hyperAST"}[d['type']]),
      //domain: [0, 40],
      //facet: "include",
      filter: d => d.matching>.0,
      strokeWidth: d => d.matched_decl_count/max_matched_decls.get(d.project)*6,
      clip:true,
      // curve:'basis',
    });
  const a = (d,t) => {
    const v = data.filter(x=>x.project===d.project && x.type===d.type).map(d=>d.matching).filter(x=>x);
    const r = v.reduce((x,y)=>x+y,0)/(v.length)
    return d3.format('.1%')(r)
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(data, Plot.selectFirst({
        x: 0,//"no",
        dx: 26,
        dy: 94,
        
        z:'project',
        text: d => a(d,'baseline'),
        //facet: "exclude",
        fill: "currentColor",
        //fill: "type",
        stroke: "white",
        facet: "include",
        fontSize:18,
        filter: d=>({baseline:"precision",evaluation:"recall"}[d['type']])==='precision',
      })),
    //boxPlotY(d_plus)
    Plot.text(data, Plot.selectFirst({
        x: 0,//"no",
        dx: 26,
        dy: 65,
        z:'project',
        text: d => a(d,'evaluation'),
        //fill: "type",
        fill: "currentColor",
        stroke: "white",
        facet: "include",
        fontSize:18,
        filter: d=>({baseline:"precision",evaluation:"recall"}[d['type']])==='recall',
      })),
  ],
  facet: {
    data: data,
    x: 'project',
    y: d => ({baseline:"precision",evaluation:"recall"}[d['type']]),//'type',
    label: null,
  },
  x: {
    nice: true,
    label:"commits",
    // ticks: 3,
    // //tickFormat: d => d < 0 ? '' : d,
    // domain: [-1,1200],
    domain: [0,1200],
    ticks: 1,
    tickRotate:-10,
  },
  color: {
    color: 'tableau10',
    legend: true
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
    tickRotate: -11,
    labelAnchor: 'center',
  },
  y: {
    grid: true,
    // inset: 6,
    domain: [.5,1.],
    //domain: [.55,1.01],
    type: scale,
  },
  grid: true,
  marginBottom: 60,
  marginRight: 30,
  width: 1350,
  height: 640,
  // fontSize:19,
})
}


function _19(Plot,data_per_module3,data_per_module2,projects_by_size){return(
Plot.plot({
  marks: [
    Plot.dot(data_per_module3, {
      x:d=> d.no+Math.random()/2,
      y: d=> 0.99+d.exact+Math.random()/1,
      fill: 'diff', 
      // stroke:'type',
      //domain: [0, 40],
      //facet: "include",
      //filter: d => d.type === 'baseline',
      r:1.3,
      clip: true,
    }),
  ],
  facet: {
    data: data_per_module2,
    x: 'project',
  },
  x: {
    nice: true,
    domain: [0,1200],
    ticks: 1,
    tickRotate:-10,
  },
  color: {
    color: 'tableau10',
    legend: true,
    type: "log",
  },
  fx: {
    axis: 'bottom',
    domain: projects_by_size,
  },
  y: {
    grid: true,
    type: "log",
  },
  grid: true,
  marginBottom: 60,
  marginRight: 60,
  marginLeft: 50,
  width: 1324,
  height: 640
})
)}

function _20(projects_scale){return(
projects_scale('AWS SDK Java')
)}

function _projects_scale(d3,projects_by_size){return(
d3.scaleOrdinal()
  .domain(projects_by_size)
  .range(projects_by_size.map((x,i)=>i))
)}

function _projects_by_size(){return(
[
//'aws-sdk-java', // take too long to execute, also not in scope for comparison it mainly contains comments and wrappers
'hadoop','flink',
//'quarkus', // too much java 17
//'guava', // uncommon maven structure, out of scope for the paper
'netty','aws-sdk-java-v2','dubbo',
//'fastjson', issues on ubuntu look at CI on github
'logging-log4j2','jenkins','javaparser','spoon',
//'aws-toolkit-eclipse', // only 111 commits
'maven','spark','skywalking','jackson-core','arthas','jacoco','junit4','gson','slf4j']
)}

function _23(md){return(
md`## Appendix`
)}

function _max_decls(d3,data){return(
new Map(Array.from(d3.group(data, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        // .filter(x=>x.type==="evaluation")
                                        // .map(x=>x.matched_decl_count)
                                        .map(x=>x.decl_count)
                                       )]))
)}

function _max_matched_decls(d3,data){return(
new Map(Array.from(d3.group(data, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        // .filter(x=>x.type==="evaluation")
                                        // .map(x=>x.matched_decl_count)
                                        .map(x=>x.matched_decl_count)
                                       )]))
)}

function _26(d3,data){return(
new Map(Array.from(d3.group(data, d => d.project),
                   ([k,v])=>[k,v]))
)}

function _ztest2(){return(
import('https://cdn.skypack.dev/@stdlib/stats-ztest2@0.0.7?min')
)}

function _28(ztest2)
{
  var x = [ 2.66, 1.5, 3.25, 0.993, 2.31, 2.41, 1.76, 2.57, 2.62, 1.23 ]; // Drawn from N(2,1)
var y = [ 4.88, 2.93, 2.96, 4.5, -0.0603, 4.62, 3.35, 2.98 ]; // Drawn from N(3,2)

var out = ztest2.default( x, y, 1.0, 2.0 );
  return out
}


function _wilcoxon(){return(
import('https://cdn.skypack.dev/@stdlib/stats-wilcoxon@0.0.7?min')
)}

function _30(wilcoxon)
{var x = [ 6, 8, 14, 16, 23, 24, 28, 29, 41, -48, 49, 56, 60, -67, 75 ];
var out = wilcoxon.default( x );
return out }


function _31(vl,dimension,data)
{
  const x = vl.x().fieldQ('no').title('commits')
  const tempMinMax = vl.markLine()
    .encode(
      x,
      vl.y().fieldQ(dimension)
      .axis({
        "titleColor": "red","orient": "left",
        grid:true,
      })
      .scale({
      //   // type:"sqrt",
        y: "independent",
      //   // "domain": [0, 30],
      //   "domain": [0, 10000],
      })
      ,
      vl.color().fieldN('type')
    )
    .transform(vl.filter('datum.no < 50'))
    ;

  const tempMid = vl.markLine()
    .encode(
      x,
      vl.y().fieldQ('decl_count')
      .axis({
        "titleColor": "#85C5A6","orient": "right",
        grid:true,
      })
      .scale({
        type:"sqrt",
        y: "independent",
      })
      ,
    // .scale({type: 'log', domain: [0.001, 1000]}),
      vl.color("red"),
    )
    .transform(vl.filter('datum.no < 50 && datum.type == "baseline"'))
    ;

  return vl.layer(tempMid, tempMinMax)
    .width(110)
    .height(150)
  // "resolve": {"scale": {"y": "independent"}}
    .facet({
      column: vl.field('project'),
      resolve: {
        scale: {"y": "independent",type:"sqrt"},
        axis: {"y": "independent",type:"sqrt"}
      }
    })
    .data(data)
    .render();
}


function _data_raw(FileAttachment){return(
(async () => [
  //{project:"spoon_old", content: await FileAttachment("summary_spoon_types_localvar2_1@1.json").json()},
  //{project:"jackson-core_old", content: await FileAttachment("summary_jackson-core_types_localvar.json").json()},
  {project:"arthas", content: await FileAttachment("summary2_arthas.json").json()},
  {project:"aws-toolkit-eclipse", content: await FileAttachment("summary_aws-toolkit-eclipse.json").json()},
  {project:"jacoco", content: await FileAttachment("summary2_jacoco.json").json()},
  {project:"junit4", content: await FileAttachment("summary2_junit4.json").json()},
  {project:"junit42", content: await FileAttachment("summary5_junit4.json").json()},
  {project:"slf4j", content: await FileAttachment("summary2_slf4j.json").json()},
  {project:"spoon", content: await FileAttachment("summary5_spoon.json").json()},
  {project:"jackson-core", content: await FileAttachment("summary5_jackson-core.json").json()},
  //{project:"jackson-core2", content: await FileAttachment("summary2_jackson-core.json").json()},
  {project:"dubbo", content: await FileAttachment("summary2_dubbo@1.json").json()},
  //{project:"dubbo2", content: await FileAttachment("summary2_dubbo.json").json()},
  //{project:"jenkins", content: await FileAttachment("summary3_jenkins.json").json()},
  {project:"jenkins", content: await FileAttachment("summary5_jenkins.json").json()},
  {project:"fastjson", content: await FileAttachment("summary5_fastjson.json").json()},
  {project:"maven", content: await FileAttachment("summary5_maven.json").json()},
  {project:"javaparser", content: await FileAttachment("summary5_javaparser.json").json()},
  {project:"logging-log4j2", content: await FileAttachment("summary5_logging-log4j2.json").json()},
  {project:"quarkus", content: await FileAttachment("summary5_quarkus.json").json()},
  {project:"flink", content: await FileAttachment("summary5_flink.json").json()},
  {project:"hadoop", content: await FileAttachment("summary5_hadoop.json").json()},
  {project:"spark", content: await FileAttachment("summary5_spark.json").json()},
  {project:"gson", content: await FileAttachment("summary5_gson@1.json").json()},
  {project:"skywalking", content: await FileAttachment("summary5_skywalking.json").json()},
  {project:"netty", content: await FileAttachment("summary5_netty.json").json()},
  {project:"guava", content: await FileAttachment("summary5_guava.json").json()},
  {project:"aws-sdk-java-v2", content: await FileAttachment("summary5_aws-sdk-java-v2@1.json").json()},
])()
)}

function _perfs_raw(FileAttachment){return(
(async () => [
  {project:"quarkus", content: await FileAttachment("perfs_quarkus.json").json()},
  {project:"flink", content: await FileAttachment("perfs_flink.json").json()},
  {project:"spoon", content: await FileAttachment("perfs_spoon.json").json()},
  {project:"javaparser", content: await FileAttachment("perfs_javaparser.json").json()},
  {project:"jenkins", content: await FileAttachment("perfs_jenkins.json").json()},
  {project:"logging-log4j2", content: await FileAttachment("perfs_logging-log4j2.json").json()},
  {project:"junit4", content: await FileAttachment("perfs_junit4.json").json()},
  {project:"maven", content: await FileAttachment("perfs_maven.json").json()},
  {project:"slf4j", content: await FileAttachment("perfs_slf4j.json").json()},
  {project:"jacoco", content: await FileAttachment("perfs_jacoco.json").json()},
  {project:"dubbo", content: await FileAttachment("perfs_dubbo.json").json()},
  {project:"jackson-core", content: await FileAttachment("perfs_jackson-core@1.json").json()},
  {project:"fastjson", content: await FileAttachment("perfs_fastjson@4.json").json()},
  {project:"aws-toolkit-eclipse", content: await FileAttachment("perfs_aws-toolkit-eclipse@1.json").json()},
  {project:"hadoop", content: await FileAttachment("perfs_hadoop@1.json").json()},
  {project:"arthas", content: await FileAttachment("perfs_arthas@2.json").json()},
  {project:"spark", content: await FileAttachment("perfs_spark.json").json()},
  {project:"gson", content: await FileAttachment("perfs_gson@1.json").json()},
  {project:"skywalking", content: await FileAttachment("perfs_skywalking.json").json()},
  {project:"netty", content: await FileAttachment("perfs_netty.json").json()},
  {project:"guava", content: await FileAttachment("perfs_guava.json").json()},
  {project:"aws-sdk-java-v2", content: await FileAttachment("perfs_aws-sdk-java-v2@1.json").json()},
])()
)}

function _full_hadoop(FileAttachment){return(
FileAttachment("fullperfs_hadoop.json").json()
)}

function _35(d3,full_hadoop){return(
(t => 'full_hadoop time = ' + 
 Math.trunc(t/60/60/1000) + 'h' + 
 d3.timeFormat('%M')(new Date(t)) + 'm' + 
 d3.timeFormat('%S')(new Date(t)))(
  new Date(full_hadoop
           .filter(x=>x.processor === 'evaluation')
           .map(x=> x.construction_perfs.time)
           .reduce((x,y)=>x+y)/1000/1000))
)}

function _36(d3,full_hadoop){return(
(z=>'full_hadoop memory = ' + 
 d3.format('.2~s')(z) 
 + 'b')(
  new Date(full_hadoop
           .filter(x=>x.processor === 'evaluation')
           .map(x=> x.construction_perfs.memory)
           .reduce((x,y)=>x+y)))
)}

function _perfs(perfs_raw,d3){return(
perfs_raw.map(xx=> xx.content.sort(function(x, y){
   return x.stats!==undefined ? d3.ascending(x.no, y.no) : d3.ascending(x.info.no, y.info.no);
}).map(x => ({
  project:xx.project,
  type:x.processor,
  construction_time:x.construction_perfs.time/1000/1000/1000,
  construction_memory_fooprint:x.construction_perfs.memory/1000/1000,
  // search_time:x.search_perfs.time/1000/1000/1000,
  // with_search_memory_fooprint:x.search_perfs.baseline.memory/1024/1024,
  no:x.info.no,
  commit:x.info.commit,
}))).flat()
)}

function _compute_ratios(){return(
(f,x,d) =>{
  return (([t,x])=>x/t)(x.map(x=> x.content).map(x=>[x.exact_decls_matches, x.exact_decls_matches*(f(x)===null?d:f(x))]).reduce((a,x) => 
        [a[0]+x[0],a[1]+x[1]]
  , [0,0]))
}
)}

function _count_refs(){return(
(f,x) =>{
  return x.map(x=> x.content).map(x=>x.exact_decls_matches * (x.exact_decls_matches*(f(x)===null?0:f(x)))).reduce((a,x) => 
        a+x
  , 0)
}
)}

function _count_decls(){return(
(f,x) =>{
  return x.map(x=> x.content).map(x=>f(x)).reduce((a,x) => 
        a+x
  , 0)
}
)}

function _formatorv1(){return(
{
  baseline: (xx,x) => ({
  project:xx.project,
  type:"baseline",
  matching:x.stats.overall_overestimation_rate,
  construction_time:x.perfs_baseline.construction_time/1000/1000/1000,
  search_time:x.perfs_baseline.search_time/1000/1000/1000,
  construction_memory_fooprint:x.perfs_baseline.construction_memory_fooprint/1000/1000,
  with_search_memory_fooprint:x.perfs_baseline.with_search_memory_fooprint/1000/1000,
  ref_count:x.stats.exact_decls_matches*x.stats.mean_of_exact_references,
  decl_count:x.stats.exact_decls_matches,
  no:x.no,
}),
  evaluated: (xx,x) => ({
  project:xx.project,
  type:"evaluation",
  matching:x.stats.overall_success_rate,
  construction_time:x.perfs_evaluated.construction_time/1000/1000/1000,
  search_time:x.perfs_evaluated.search_time/1000/1000/1000,
  construction_memory_fooprint:x.perfs_evaluated.construction_memory_fooprint/1000/1000,
  with_search_memory_fooprint:x.perfs_evaluated.with_search_memory_fooprint/1000/1000,
  ref_count:x.stats.exact_decls_matches*x.stats.mean_of_exact_references,
  decl_count:x.stats.remaining_decls_in_tool_results+x.stats.exact_decls_matches,
  no:x.no,
}),
}
)}

function _formatorv2(compute_ratios,count_refs,count_decls){return(
{
  baseline: (xx,x) => ({
  project:xx.project,
  type:"baseline",
  matching: compute_ratios(x=>x.overall_overestimation_rate,x.relations_stats,0),
  construction_time:x.construction_perfs.baseline.time/1000/1000/1000,
  search_time:x.search_perfs.baseline.time/1000/1000/1000,
  construction_memory_fooprint:x.construction_perfs.baseline.memory/1000/1000,
  with_search_memory_fooprint:x.search_perfs.baseline.memory/1000/1000,
  ref_count:count_refs(x=>x.mean_of_exact_references,x.relations_stats),
  decl_count_rem:count_decls(x=>x.remaining_decls_in_baseline,x.relations_stats),
  decl_count:count_decls(x=>x.remaining_decls_in_baseline+x.exact_decls_matches,x.relations_stats),
  matched_decl_count:count_decls(x=>x.exact_decls_matches,x.relations_stats),
  decl_per_module: x.relations_stats.map(y=>({name:y.module,exact:y.content.exact_decls_matches,
                                              rem:y.content.remaining_decls_in_baseline})),
  no:x.info.no,
  commit:x.info.commit,
}),
  evaluated: (xx,x) => ({
  project:xx.project,
  type:"evaluation",
  matching: compute_ratios(x=>x.overall_success_rate,x.relations_stats,1),
  construction_time:x.construction_perfs.evaluated.time/1000/1000/1000,
  search_time:x.search_perfs.evaluated.time/1000/1000/1000,
  construction_memory_fooprint:x.construction_perfs.evaluated.memory/1000/1000,
  with_search_memory_fooprint:x.search_perfs.evaluated.memory/1000/1000,
  ref_count:count_refs(x=>x.mean_of_exact_references,x.relations_stats),
  decl_count_rem:count_decls(x=>x.remaining_decls_in_tool_results,x.relations_stats),
  decl_count:count_decls(x=>x.remaining_decls_in_tool_results+x.exact_decls_matches,x.relations_stats),
  matched_decl_count:count_decls(x=>x.exact_decls_matches,x.relations_stats),
  decl_per_module: x.relations_stats.map(y=>({name:y.module,exact:y.content.exact_decls_matches,
                                              rem:y.content.remaining_decls_in_tool_results})),
  no:x.info.no,
  commit:x.info.commit,
}),
}
)}

function _43(data){return(
data.filter(x=>x.project === "fastjson")
)}

function _data_per_module(data){return(
data.map(x=> [
              ...x.decl_per_module.map(y=>({decl_nature:"exact", value:y.exact, name:y.name, project:x.project, type:x.type, no: x.no, commit:x.commit})),
              ...x.decl_per_module.map(y=>({decl_nature:"remaining", value:y.rem, name:y.name, project:x.project, type:x.type, no: x.no, commit:x.commit}))
        ]).flat()
)}

function _data_per_module2(data){return(
data.map(x=> 
              x.decl_per_module.map(y=>({exact:y.exact, remaining:y.rem, name:y.name, project:x.project, type:x.type, no: x.no, commit:x.commit}))
        ).flat()
)}

function _data_per_module3(d3,data_per_module2){return(
[...d3.group(data_per_module2, d => d.project, d=> d.no, d=> d.name, d=> d.type).values()]
  .map(x=> 
       [...x.values()]
       .map(x=> [...x.values()].map(x=> {
         return {
           ...x.get('evaluation')[0],
           diff: Math.abs(x.get('evaluation')[0].remaining - x.get('baseline')[0].remaining),
           ratio: x.get('evaluation')[0].remaining / x.get('baseline')[0].remaining,
           baseline: x.get('baseline')[0],
           evaluated: x.get('evaluation')[0],
         }
         }).flat()
       ).flat()
    ).flat()
)}

function _reserialization(FileAttachment){return(
FileAttachment("reserialization.csv").csv()
)}

function _48(reserialization){return(
reserialization.map(x=>x.ok).reduce((x,y)=>x+y)/(
  reserialization.map(x=>x.ok).reduce((x,y)=>x+y)+
  reserialization.map(x=>x.bad).reduce((x,y)=>x+y))
)}

function _data(data_raw,d3,formatorv1,formatorv2){return(
data_raw.map(xx=> xx.content.sort(function(x, y){
   return x.stats!==undefined ? d3.ascending(x.no, y.no) : d3.ascending(x.info.no, y.info.no);
}).map(x=>x.stats!==undefined ? formatorv1.baseline(xx,x) : formatorv2.baseline(xx,x)
//        ({
//   project:xx.project,
//   type:"baseline",
//   matching:x.stats.overall_overestimation_rate,
//   construction_time:x.perfs_baseline.construction_time/1000/1000/1000,
//   search_time:x.perfs_baseline.search_time/1000/1000/1000,
//   construction_memory_fooprint:x.perfs_baseline.construction_memory_fooprint/1024/1024,
//   with_search_memory_fooprint:x.perfs_baseline.with_search_memory_fooprint/1024/1024,
//   ref_count:x.stats.exact_decls_matches*x.stats.mean_of_exact_references,
//   no:x.no,
// })
      ).concat(xx.content.sort(function(x, y){
   return x.stats!==undefined ? d3.ascending(x.no, y.no) : d3.ascending(x.info.no, y.info.no);
}).map(x=>x.stats!==undefined ? formatorv1.evaluated(xx,x) : formatorv2.evaluated(xx,x)
//        ({
//   project:xx.project,
//   type:"evaluation",
//   matching:x.stats.overall_success_rate,
//   construction_time:x.perfs_evaluated.construction_time/1000/1000/1000,
//   search_time:x.perfs_evaluated.search_time/1000/1000/1000,
//   construction_memory_fooprint:x.perfs_evaluated.construction_memory_fooprint/1024/1024,
//   with_search_memory_fooprint:x.perfs_evaluated.with_search_memory_fooprint/1024/1024,
//   ref_count:x.stats.exact_decls_matches*x.stats.mean_of_exact_references,
//   no:x.no,
// })
      )
)).flat()
)}

function _seedrandom(require){return(
require('seedrandom')
)}

export default function define(runtime, observer) {
  const main = runtime.module();
  function toString() { return this.url; }
  const fileAttachments = new Map([
    ["summary_aws-toolkit-eclipse.json", {url: new URL("./files/079082cabf8388747699c9d9f669dd15397611ab7c99b32e217ca19454666c137e51e1da0980af3e830448dd0b8a407a4a9d41db9cc5d19db95b7649a28f8d37", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_junit4.json", {url: new URL("./files/5cd61dfb535b2b5656d4343293a9447721a3ba004a66398ae7742f4b770a337dc3ca1cc9f95ba9b3bb3be298daa2f3dddc9992004c39866f6daf5dd51b0bef44", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_arthas.json", {url: new URL("./files/b5ab04f7bb5df7176f0fa1d0d4a20594c83bbf266a784f645a8c6ac332391a287e6f977a6579d5c9f3614d35b2b112c6d61e0c99d74d0e5ccb62ce41d1ecc406", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_slf4j.json", {url: new URL("./files/6b85f13ca5e8f37ed8bda9c1a74a4f76ab69e641ee7844ec179545ea2f6c9662f9584f2397b1a2b284ab9189dfb923e20fe64bb1cc5f750ca816b4eb08d9a629", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_jacoco.json", {url: new URL("./files/872a974fbafd70515be83fbed09d4c57e7e31ce096fcaba919392c9ce51489942d30a3c3a42f5541583ad29f035b4821396f22727a3f1a7bb980465fc48b086e", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_dubbo@1.json", {url: new URL("./files/65bff6fc40d988e8eb57b727312e0d7e4a1a827ab45d0111c70cf0664d6be981aa419c27fe12018c5ed1b1cd5330caece8b60a1f99828d8284e15d3448d12110", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_spoon.json", {url: new URL("./files/2a7b90d90111bc348eaa9aa45e39a0c7b4d22997987096fca0c9d0ebbfb374285bf59d33a8fc828f323584057af8b1b35ed2f5b285181ca695e9bb4c67c05b34", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_maven.json", {url: new URL("./files/cd26665521f14a8bd85f21d7448353ba7125a4e575e1b2cd778b97551e3ecc09f0e2fe406dbe6cc2e2e41d79ff35a37162dc6513e44dd942939b7a496b121c0a", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_javaparser.json", {url: new URL("./files/d54c71d6b6f6018ef4ab9277a4633ea40ec6dfa1e57ae30b4424d351500161066706199cf789b238d282a16035e1f973f495ee225e0fede1159232eab83d61b8", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_jenkins.json", {url: new URL("./files/287e3b0341f25c59aaddddf51309243fd5ff1b02bd70ac0219f7192b5ee3eaba99439db4db93905a523b4eaf90cdd38b896094767d2daf529616b738311601ae", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_logging-log4j2.json", {url: new URL("./files/223fa98a1c975403b1b33b9a22f46c41eca2957ca41c3361fda23b3369b1d9c1fa7263c33af758d61357bfadd845045ba8c486c04527d952aa850fb402f7e729", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_junit4.json", {url: new URL("./files/0ce12ade6b645b3cd11e0c7c28cf90a055d3bdd573854be1d1a4c0be378fb1c85145a55190a1bf7aa84eacd1b6fd644fe8b16cf58d3f1e268e76776f7fc78b9c", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_quarkus.json", {url: new URL("./files/df6b810156f87fe8549de60041f502cff6c25d5c520c9238f213b165b03de22e5b29a557f35371fdef07b42b63807b81352d35d5a5ed447bbdd35f3dbebbaade", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_flink.json", {url: new URL("./files/882f86882951a940dc8aa4054f82e1a5e2c7b0f16225eca60b8ccb1b35f192364d32e0abd668aef6ddbf315cfc8ed26998c99f6ec372d62c110185ccf7045003", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_spoon.json", {url: new URL("./files/fc3514afd580b0b0c4ef204915b6ab1f541995787d0686796fba7dd6450357ea631b596cd1af37d223cc02d091e40917d1ee6c87b0714794dbdbf733249a86e2", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_flink.json", {url: new URL("./files/58a1ae11fc3b3e0a06ab0209eab45666932efedfc61e3a16b3921f2e9872bb85e0ec34e83f0c1db09d104b9c9b03da901ec6e7ff9780d7f4eb379e7552ea46e1", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_quarkus.json", {url: new URL("./files/e4fec22552657ef54f2ee1a8a5049e8fdc88634491fc246d6f1f37e0a7b691e2b88c5f893be8a4a7db1ab0b46b93f21c363082397ebe92c519710923cbda525b", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_maven.json", {url: new URL("./files/9ea3ace508570b48f41042ac40455543c3f41acbf10ae5163e5e8778539c8fbb6052fb33c93d3519c431762187b1ce8a8a5ff4ef649b3110bcbb97941861d140", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_junit4.json", {url: new URL("./files/c8199e94110819c6f1317585cfe8dccad67fb31744b4700674c40bf2a6f93ce3746a8bce517504fb5ad0a500e305d4e3b6f092fb699faaac120e48361d37effc", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_logging-log4j2.json", {url: new URL("./files/873498e24725a557233482b75e512948d97d4beccc2d68620d3ec0b449e80cab723b0e32ab17079c53ddd71bd2e4c8aae8f9aa4eb12a088c9550d0e80dc5d286", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jenkins.json", {url: new URL("./files/430dc8c7ac8ba3d84326d21038a98971515ba62952894b45bb4088a2a52916e26716e8d854619f900ce8865fb157ce4a548188dfc5aef75cf860b5e79fe41e78", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_javaparser.json", {url: new URL("./files/0bf1cbc759876ebf4b798cfddfdcf3ce7cb2a9bab0a38b88994acae68aebd49a98a34ae431ac085b8c057ccfc61dbaa5fed12b72ba1fce2cbcc061fc055114a9", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_dubbo.json", {url: new URL("./files/1b3ffab96169ac9a8476d2aa35c07977b1365fc6b6efd92a38cd0103a43a7ddaf3d6bb80ea6c8d7d169ab1b57b786dc956cf08d8fca6aa6eb35d98e448988fa9", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jacoco.json", {url: new URL("./files/cc9852de6934f49e78004c87348c2e4b88f2110369783fdc367664ee616a70e1ced57dd3168eb6a05d24353e296fce97c84b24b38597b3069d4af0d85f01891f", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_slf4j.json", {url: new URL("./files/54b7367d788b5c3d72aaa1a468c4c57c3c9fec620dffc846ac21a8211aeed26721980bf71c7803ff3d7cc3cd34496f32ede74e8f1c5c9a7fd1ef7403d85e3839", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_arthas@2.json", {url: new URL("./files/11a53708225bc5a0e0ce7cf572b4530ba483084469090cda247cb92030ef33840d53073bff9ec80e9246a4472624f174c66e479c793005a784acf615cf065837", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_aws-toolkit-eclipse@1.json", {url: new URL("./files/7045caa7ae82eeed1e075fa9f0a2b1cbcaf7e0444fb6f399f74337f144696fcec42cd8757413a6299f4e391f913f5a114acf88843cf59170fa6d88c70eba05c0", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_hadoop@1.json", {url: new URL("./files/e523d8dfe30b1d613280d12badc2ec1affc1c7f8e0672a68f5a769ebc75fca6df63dbd1f67d7c690dace7d8574c895aeacef7abf5959998d86ea356f3f160c23", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_spark.json", {url: new URL("./files/67e5aacb93961981cbd993b3d0c733c69ffc654abff471ae91c23fb8d9fed780cf983f8c2e3d155e809c1266b1eacf09b7f8a80c99fb7562730fda18c7d838c9", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_spark.json", {url: new URL("./files/2391017f50d4c89c0280b3eedb3b4619f60555559168259bcb3a197d9c27a1c2960c96da6a04dcd38003c4db55dd7637aebfefb1afd8d9123410fcfd1b2e649e", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_hadoop.json", {url: new URL("./files/43fb3c5f75947091cb459b381fe53bef0c61644309867d5350ce038e79f462a314b489e71799ec5fa6b2c8a2743024f4186bd0cb1bf8e465944503bcf032b6f0", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_gson@1.json", {url: new URL("./files/0315f8cbbaa0e32b2230c0359b567e4dc5b3d1cce7601d1af6e510ef12665b0a8e6485f925c147af179954ada28be3815201736e8f97e023bb2028ba4357b6b2", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_gson@1.json", {url: new URL("./files/a53605c625d072fb236a8f69af9ba36872e041e837a15a059ffa7481353ffbb8a7a9f34e88750de2938afa698baaa5eb1acc9dd8f704b0ab0f726176dc7f2bae", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_skywalking.json", {url: new URL("./files/bfbc23bc0178243ea47fa4b92a6af8d80d1e657116055967bbbe4489808bb5f0df9e32348c63797311f2a203eb7c341819964044c2a771822632f934eef97831", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_skywalking.json", {url: new URL("./files/c5e2d47446be3f5cc4b3e598a88edcf2cf84ececae283f39d2e9b8cec6591b61baab72c99c8be39d0b6f4381bdc6088a382fb78fef157854c403cdda53af798c", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_fastjson.json", {url: new URL("./files/c84d4bfd77d88871c591c652e182bffc76f9f32094b5d9abe87e9f8d8a3edbf362f77b04f6436c5e7a7c1f41104d9047221752811d59afb21b7f1f8aef2ebbbb", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_guava.json", {url: new URL("./files/6ecc5384e04c4d8498c43047ddb66fd81ecd93e02f4fec71d2717eea7af0baf0081d952f356e003dd1ee0e81cc3bab03cd5765a39b3759fc26ffceddf21542ba", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_guava.json", {url: new URL("./files/b4aa1f11c69ba40c890f42f1f23597c3549dc0cadc1010821fd8dde712a9c713338c64c7d4dc5ce07f5a2ef438b369f291f8443905debfa1620303a667c216e8", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_netty.json", {url: new URL("./files/8e46207a52a36664ac817f4795027dc3b72dad42515dee0232eebb9ef25bfc47a9c3dc46d55e2940eddd23f1638d4b88e7ea8e9b61b3c8cc2092d61da51c10af", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_netty.json", {url: new URL("./files/fec50da65ed2cb0f213abc6ca597bd8a6c8dd9350b71302d3135a36e7124a11d23e8ea92e69b071a2d8c07a50161c58d45080684bb412a35bad874d0c70e33dd", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_jackson-core.json", {url: new URL("./files/fb5136708e9edd65992d59de279b6d64ce09f61aba7bca051e0e2d3994bc089d921cb916a52dfa20034cf8483edad399a8db462dd748a8c4e0293d213ec03356", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_fastjson@4.json", {url: new URL("./files/6a517d29fc95ff388f4d6c79b08b92d82e7582ead411f73090473617c18ce85007615b15863e250313ed07e948459fa2546f6a62bb85b62e30b5422cdfa77bac", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jackson-core@1.json", {url: new URL("./files/e9153f11dd7b619be91bd0134036c41b31a2677e7843b08ad9b64c9d4b3a3489b6e0846c395826f2f3a4eaa9b05616514e092ae98a4f9334f0d00fa466161d27", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_aws-sdk-java-v2@1.json", {url: new URL("./files/1c59e257eea7b263e6986ae2ecf52cf5c2dd3f18493e46568e79266fe2fecf116f5c3d1be16160c9dc759d2097c3c8f5061321b244a474f75b190d880f949d10", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_aws-sdk-java-v2@1.json", {url: new URL("./files/6bbd2047015c5847af493fe274373efe91be0314886653d05d8aa4577737adeb998009597fc747b06919898ed92f6d0ca93bbb6af85271a674b313ae9fcdcf98", import.meta.url), mimeType: "application/json", toString}],
    ["fullperfs_hadoop.json", {url: new URL("./files/2d014318cc18409785804fc3f6488b74c9572c39ca093c4854633c45832ba6b0f76fa0575c1f4866b2d5ec6ba8dbe4ac9adfd934b1ab80a94eb592aa010e87c4", import.meta.url), mimeType: "application/json", toString}],
    ["reserialization.csv", {url: new URL("./files/68dc6fc77d953756fced180821e2f59904ee77ff080ec528c6d9751d03175c8ba0b5a9ed4f83660e2fab15b98dda9022ae990f37e8fd05b8d2d17fe4a5ef9475", import.meta.url), mimeType: "text/csv", toString}]
  ]);
  main.builtin("FileAttachment", runtime.fileAttachments(name => fileAttachments.get(name)));
  main.variable(observer()).define(["md"], _1);
  main.variable(observer()).define(["md"], _2);
  main.variable(observer()).define(["html"], _3);
  main.variable(observer()).define(["perfs","projects_by_size","Plot","decls_factor","max_matched_decls","reg","d3","scale"], _4);
  main.variable(observer()).define(["perfs","projects_by_size","Plot","decls_factor","max_matched_decls","reg","d3","scale"], _5);
  main.variable(observer()).define(["data","Plot","decls_factor","max_matched_decls","reg","d3","projects_by_size","scale"], _6);
  main.variable(observer("viewof dimension")).define("viewof dimension", ["Inputs"], _dimension);
  main.variable(observer("dimension")).define("dimension", ["Generators", "viewof dimension"], (G, _) => G.input(_));
  main.variable(observer("viewof scale")).define("viewof scale", ["Inputs"], _scale);
  main.variable(observer("scale")).define("scale", ["Generators", "viewof scale"], (G, _) => G.input(_));
  main.variable(observer("viewof decls_factor")).define("viewof decls_factor", ["Inputs"], _decls_factor);
  main.variable(observer("decls_factor")).define("decls_factor", ["Generators", "viewof decls_factor"], (G, _) => G.input(_));
  main.variable(observer()).define(["data","Plot","dimension","decls_factor","max_matched_decls","reg","projects_by_size","scale"], _10);
  main.variable(observer("reg")).define("reg", ["require"], _reg);
  main.variable(observer("viewof dimension_decls")).define("viewof dimension_decls", ["Inputs"], _dimension_decls);
  main.variable(observer("dimension_decls")).define("dimension_decls", ["Generators", "viewof dimension_decls"], (G, _) => G.input(_));
  main.variable(observer()).define(["Plot","data","dimension_decls","projects_scale","projects_by_size"], _13);
  const child1 = runtime.module(define1);
  main.import("vl", child1);
  main.variable(observer()).define(["Plot","data","dimension","scale"], _15);
  main.variable(observer("precision")).define("precision", ["d3","data"], _precision);
  main.variable(observer("recall")).define("recall", ["d3","data"], _recall);
  main.variable(observer()).define(["Plot","data","max_matched_decls","d3","projects_by_size","scale"], _18);
  main.variable(observer()).define(["Plot","data_per_module3","data_per_module2","projects_by_size"], _19);
  main.variable(observer()).define(["projects_scale"], _20);
  main.variable(observer("projects_scale")).define("projects_scale", ["d3","projects_by_size"], _projects_scale);
  main.variable(observer("projects_by_size")).define("projects_by_size", _projects_by_size);
  main.variable(observer()).define(["md"], _23);
  main.variable(observer("max_decls")).define("max_decls", ["d3","data"], _max_decls);
  main.variable(observer("max_matched_decls")).define("max_matched_decls", ["d3","data"], _max_matched_decls);
  main.variable(observer()).define(["d3","data"], _26);
  main.variable(observer("ztest2")).define("ztest2", _ztest2);
  main.variable(observer()).define(["ztest2"], _28);
  main.variable(observer("wilcoxon")).define("wilcoxon", _wilcoxon);
  main.variable(observer()).define(["wilcoxon"], _30);
  main.variable(observer()).define(["vl","dimension","data"], _31);
  main.variable(observer("data_raw")).define("data_raw", ["FileAttachment"], _data_raw);
  main.variable(observer("perfs_raw")).define("perfs_raw", ["FileAttachment"], _perfs_raw);
  main.variable(observer("full_hadoop")).define("full_hadoop", ["FileAttachment"], _full_hadoop);
  main.variable(observer()).define(["d3","full_hadoop"], _35);
  main.variable(observer()).define(["d3","full_hadoop"], _36);
  main.variable(observer("perfs")).define("perfs", ["perfs_raw","d3"], _perfs);
  main.variable(observer("compute_ratios")).define("compute_ratios", _compute_ratios);
  main.variable(observer("count_refs")).define("count_refs", _count_refs);
  main.variable(observer("count_decls")).define("count_decls", _count_decls);
  main.variable(observer("formatorv1")).define("formatorv1", _formatorv1);
  main.variable(observer("formatorv2")).define("formatorv2", ["compute_ratios","count_refs","count_decls"], _formatorv2);
  main.variable(observer()).define(["data"], _43);
  main.variable(observer("data_per_module")).define("data_per_module", ["data"], _data_per_module);
  main.variable(observer("data_per_module2")).define("data_per_module2", ["data"], _data_per_module2);
  main.variable(observer("data_per_module3")).define("data_per_module3", ["d3","data_per_module2"], _data_per_module3);
  main.variable(observer("reserialization")).define("reserialization", ["FileAttachment"], _reserialization);
  main.variable(observer()).define(["reserialization"], _48);
  main.variable(observer("data")).define("data", ["data_raw","d3","formatorv1","formatorv2"], _data);
  main.variable(observer("seedrandom")).define("seedrandom", ["require"], _seedrandom);
  const child2 = runtime.module(define2);
  main.import("Plot", child2);
  return main;
}
