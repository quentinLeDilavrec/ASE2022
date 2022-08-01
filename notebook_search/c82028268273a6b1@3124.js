import define1 from "./c4134a8cb65e3e72@465.js";

function _1(md){return(
md`# Validity and performances of references search`
)}

function _article_results(Inputs){return(
Inputs.toggle({label: "Show results presented in article", value: true})
)}

function _3(md){return(
md`You can visualize results that you computed with the reproduction package, by adding the path to the Json containing performance results (one file per line). For example, using \`run_benchmark_simp.sh\` you should write \`my_spoon results_simp/summary_spoon.json\`. As another example, using \`run_benchmark_all.sh\` you should write \`my_spoon results/summary_spoon.json\`.

If you want to test more repositories and commits, you should either modify variables (SHORTNAME,REPO,BEFORE,AFTER) in  \`run_benchmark_simp.sh\` or modify entries in \`hyperAST/all.csv\` and run \`run_benchmark_all.sh\`.`
)}

function _additional_results_text(Inputs){return(
Inputs.textarea({label: "Result computed with reproduction package", submit: true})
)}

function _additional_projects_name(additional_results){return(
additional_results.map(x=>x.project)
)}

function _validity(data,projects_name,additional_projects_name,projects_by_size,Plot,max_matched_decls,d3,scale)
{
  let d_plus = data.filter(x=> !projects_name.includes(x.project) || projects_by_size.includes(x.project));
  const lines = 
    Plot.line(d_plus, {
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
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type).map(d=>d.matching).filter(x=>x);
    const r = v.reduce((x,y)=>x+y,0)/(v.length)
    return d3.format('.1%')(r)
  };
return Plot.plot({
  marks: [
    lines,
    Plot.text(d_plus, Plot.selectFirst({
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
    Plot.text(d_plus, Plot.selectFirst({
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
    data: d_plus,
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
    domain: [...projects_by_size,...additional_projects_name],
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


function _search_time(data,projects_name,additional_projects_name,projects_by_size,Plot,decls_factor,max_matched_decls,reg,d3,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = data.filter(x=> !projects_name.includes(x.project) || projects_by_size.includes(x.project)).map(d=> ({...d, type: (f_type[d.type]) }));
  const dimension = "search_time";
  const lines = 
    Plot.line(d_plus, {
      x:'no',
      y: dimension, 
      stroke: 'type',
      facet: "include",
      clip: true,
      filter: d => d.no<1000,
      strokeWidth: decls_factor ? d => d.matched_decl_count/max_matched_decls.get(d.project)*12 : 2,
    });
  const r = (X,Y) => reg.regressionLinear().x((i) => X[i]).y((i) => Y[i]);
  const a = (d) => {
    const v = d_plus.filter(x=>x.project===d.project && x.type===d.type)
    const s = r(v.map(d=>d.no),v.map(d=>d[dimension]))(v.map((x,i)=>i));
    let z = v.map(d=>d[dimension]*50).reduce((x,y)=>x+y,0)/1000
    let t = z*1000
    return Math.trunc(t/60/1000) + 'm' + d3.timeFormat('%S')(new Date(t))
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
    domain: [...projects_by_size,...additional_projects_name],
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


function _decls_factor(Inputs){return(
Inputs.toggle({ label: 'Factor in declarations', value: false })
)}

function _scale(Inputs){return(
Inputs.select(['linear'/*,'log'*/], { label: 'Scale', value: 'linear' })
)}

function _reg(require){return(
require("d3-regression@1")
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

function _13(md){return(
md`## Appendix`
)}

function _max_decls(d3,data){return(
new Map(Array.from(d3.group(data, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        .map(x=>x.decl_count)
                                       )]))
)}

function _max_matched_decls(d3,data){return(
new Map(Array.from(d3.group(data, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        .map(x=>x.matched_decl_count)
                                       )]))
)}

function _16(data){return(
data.filter(d=>d.matching < .7)
)}

function _data(data_raw,additional_results,d3,formator){return(
[...data_raw,...additional_results].map(xx=>
    xx.content
      .sort((x, y)=> d3.ascending(x.info.no, y.info.no))
      .map(x=>formator.baseline(xx,x))
    .concat(
    xx.content
      .sort((x, y) => d3.ascending(x.info.no, y.info.no))
      .map(x=>formator.evaluated(xx,x))
)).flat()
)}

function _projects_name(data_raw){return(
data_raw.map(x=>x.project)
)}

function _data_raw(article_results,FileAttachment){return(
article_results ? (async () => [
  {project:"arthas", content: await FileAttachment("summary2_arthas.json").json()},
  {project:"aws-toolkit-eclipse", content: await FileAttachment("summary_aws-toolkit-eclipse.json").json()},
  {project:"jacoco", content: await FileAttachment("summary2_jacoco.json").json()},
  //{project:"junit4", content: await FileAttachment("summary2_junit4.json").json()},
  {project:"junit4", content: await FileAttachment("summary5_junit4.json").json()},
  {project:"slf4j", content: await FileAttachment("summary2_slf4j.json").json()},
  {project:"spoon", content: await FileAttachment("summary_2_spoon.json").json()},//summary5_spoon.json").json()},
  {project:"jackson-core", content: await FileAttachment("summary5_jackson-core.json").json()},
  {project:"dubbo", content: await FileAttachment("summary2_dubbo@1.json").json()},
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
])() : []
)}

function _formator(compute_ratios,count_refs,count_decls){return(
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

function _additional_results(additional_results_text,FA)
{
  let lines = additional_results_text.split("\n").map(x=>x.trim()).filter(x=> x.length>0);
  let projects = lines.map(x=>x.split(" ")).filter(x=>x.length===2).map(async([p,f,_])=>({project:p, content: await FA(""+f).json()}))
  return Promise.all(projects)
}


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

function _seedrandom(require){return(
require('seedrandom')
)}

function _FA(FileAttachment){return(
FileAttachment
)}

export default function define(runtime, observer) {
  const main = runtime.module();
  function toString() { return this.url; }
  const fileAttachments = new Map([
    ["summary_2_spoon.json", {url: new URL("./files/81d856c77b0964558afa0d1b30f8a96cdf836c946debfbfd28e4fabba3bab279d8d89867901656c001b7214d467323fff1e241603b3f78539528365600f80eb1.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_flink.json", {url: new URL("./files/882f86882951a940dc8aa4054f82e1a5e2c7b0f16225eca60b8ccb1b35f192364d32e0abd668aef6ddbf315cfc8ed26998c99f6ec372d62c110185ccf7045003.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_spark.json", {url: new URL("./files/2391017f50d4c89c0280b3eedb3b4619f60555559168259bcb3a197d9c27a1c2960c96da6a04dcd38003c4db55dd7637aebfefb1afd8d9123410fcfd1b2e649e.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_hadoop.json", {url: new URL("./files/43fb3c5f75947091cb459b381fe53bef0c61644309867d5350ce038e79f462a314b489e71799ec5fa6b2c8a2743024f4186bd0cb1bf8e465944503bcf032b6f0.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_jackson-core.json", {url: new URL("./files/fb5136708e9edd65992d59de279b6d64ce09f61aba7bca051e0e2d3994bc089d921cb916a52dfa20034cf8483edad399a8db462dd748a8c4e0293d213ec03356.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_netty.json", {url: new URL("./files/fec50da65ed2cb0f213abc6ca597bd8a6c8dd9350b71302d3135a36e7124a11d23e8ea92e69b071a2d8c07a50161c58d45080684bb412a35bad874d0c70e33dd.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_guava.json", {url: new URL("./files/6ecc5384e04c4d8498c43047ddb66fd81ecd93e02f4fec71d2717eea7af0baf0081d952f356e003dd1ee0e81cc3bab03cd5765a39b3759fc26ffceddf21542ba.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_fastjson.json", {url: new URL("./files/c84d4bfd77d88871c591c652e182bffc76f9f32094b5d9abe87e9f8d8a3edbf362f77b04f6436c5e7a7c1f41104d9047221752811d59afb21b7f1f8aef2ebbbb.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_skywalking.json", {url: new URL("./files/c5e2d47446be3f5cc4b3e598a88edcf2cf84ececae283f39d2e9b8cec6591b61baab72c99c8be39d0b6f4381bdc6088a382fb78fef157854c403cdda53af798c.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_gson@1.json", {url: new URL("./files/0315f8cbbaa0e32b2230c0359b567e4dc5b3d1cce7601d1af6e510ef12665b0a8e6485f925c147af179954ada28be3815201736e8f97e023bb2028ba4357b6b2.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_jenkins.json", {url: new URL("./files/287e3b0341f25c59aaddddf51309243fd5ff1b02bd70ac0219f7192b5ee3eaba99439db4db93905a523b4eaf90cdd38b896094767d2daf529616b738311601ae.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_quarkus.json", {url: new URL("./files/df6b810156f87fe8549de60041f502cff6c25d5c520c9238f213b165b03de22e5b29a557f35371fdef07b42b63807b81352d35d5a5ed447bbdd35f3dbebbaade.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_junit4.json", {url: new URL("./files/0ce12ade6b645b3cd11e0c7c28cf90a055d3bdd573854be1d1a4c0be378fb1c85145a55190a1bf7aa84eacd1b6fd644fe8b16cf58d3f1e268e76776f7fc78b9c.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_logging-log4j2.json", {url: new URL("./files/223fa98a1c975403b1b33b9a22f46c41eca2957ca41c3361fda23b3369b1d9c1fa7263c33af758d61357bfadd845045ba8c486c04527d952aa850fb402f7e729.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_javaparser.json", {url: new URL("./files/d54c71d6b6f6018ef4ab9277a4633ea40ec6dfa1e57ae30b4424d351500161066706199cf789b238d282a16035e1f973f495ee225e0fede1159232eab83d61b8.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_maven.json", {url: new URL("./files/cd26665521f14a8bd85f21d7448353ba7125a4e575e1b2cd778b97551e3ecc09f0e2fe406dbe6cc2e2e41d79ff35a37162dc6513e44dd942939b7a496b121c0a.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_arthas.json", {url: new URL("./files/b5ab04f7bb5df7176f0fa1d0d4a20594c83bbf266a784f645a8c6ac332391a287e6f977a6579d5c9f3614d35b2b112c6d61e0c99d74d0e5ccb62ce41d1ecc406.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_slf4j.json", {url: new URL("./files/6b85f13ca5e8f37ed8bda9c1a74a4f76ab69e641ee7844ec179545ea2f6c9662f9584f2397b1a2b284ab9189dfb923e20fe64bb1cc5f750ca816b4eb08d9a629.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_jacoco.json", {url: new URL("./files/872a974fbafd70515be83fbed09d4c57e7e31ce096fcaba919392c9ce51489942d30a3c3a42f5541583ad29f035b4821396f22727a3f1a7bb980465fc48b086e.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary2_dubbo@1.json", {url: new URL("./files/65bff6fc40d988e8eb57b727312e0d7e4a1a827ab45d0111c70cf0664d6be981aa419c27fe12018c5ed1b1cd5330caece8b60a1f99828d8284e15d3448d12110.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary_aws-toolkit-eclipse.json", {url: new URL("./files/079082cabf8388747699c9d9f669dd15397611ab7c99b32e217ca19454666c137e51e1da0980af3e830448dd0b8a407a4a9d41db9cc5d19db95b7649a28f8d37.json", import.meta.url), mimeType: "application/json", toString}],
    ["summary5_aws-sdk-java-v2@1.json", {url: new URL("./files/6bbd2047015c5847af493fe274373efe91be0314886653d05d8aa4577737adeb998009597fc747b06919898ed92f6d0ca93bbb6af85271a674b313ae9fcdcf98.json", import.meta.url), mimeType: "application/json", toString}]
  ]);
  let read_result = (name) => {
    let n = name;//.replace(/[^a-zA-Z0-9]/gi, '_');
    return {url: new URL("./"+n, import.meta.url), mimeType: "application/json", toString}
  };
  main.builtin("FileAttachment", runtime.fileAttachments(name => fileAttachments.get(name) || read_result(name)));
  main.variable(observer()).define(["md"], _1);
  main.variable(observer("viewof article_results")).define("viewof article_results", ["Inputs"], _article_results);
  main.variable(observer("article_results")).define("article_results", ["Generators", "viewof article_results"], (G, _) => G.input(_));
  main.variable(observer()).define(["md"], _3);
  main.variable(observer("viewof additional_results_text")).define("viewof additional_results_text", ["Inputs"], _additional_results_text);
  main.variable(observer("additional_results_text")).define("additional_results_text", ["Generators", "viewof additional_results_text"], (G, _) => G.input(_));
  main.variable(observer("additional_projects_name")).define("additional_projects_name", ["additional_results"], _additional_projects_name);
  main.variable(observer("validity")).define("validity", ["data","projects_name","additional_projects_name","projects_by_size","Plot","max_matched_decls","d3","scale"], _validity);
  main.variable(observer("search_time")).define("search_time", ["data","projects_name","additional_projects_name","projects_by_size","Plot","decls_factor","max_matched_decls","reg","d3","scale"], _search_time);
  main.variable(observer("viewof decls_factor")).define("viewof decls_factor", ["Inputs"], _decls_factor);
  main.variable(observer("decls_factor")).define("decls_factor", ["Generators", "viewof decls_factor"], (G, _) => G.input(_));
  main.variable(observer("viewof scale")).define("viewof scale", ["Inputs"], _scale);
  main.variable(observer("scale")).define("scale", ["Generators", "viewof scale"], (G, _) => G.input(_));
  main.variable(observer("reg")).define("reg", ["require"], _reg);
  main.variable(observer("projects_scale")).define("projects_scale", ["d3","projects_by_size"], _projects_scale);
  main.variable(observer("projects_by_size")).define("projects_by_size", _projects_by_size);
  main.variable(observer()).define(["md"], _13);
  main.variable(observer("max_decls")).define("max_decls", ["d3","data"], _max_decls);
  main.variable(observer("max_matched_decls")).define("max_matched_decls", ["d3","data"], _max_matched_decls);
  main.variable(observer()).define(["data"], _16);
  main.variable(observer("data")).define("data", ["data_raw","additional_results","d3","formator"], _data);
  main.variable(observer("projects_name")).define("projects_name", ["data_raw"], _projects_name);
  main.variable(observer("data_raw")).define("data_raw", ["article_results","FileAttachment"], _data_raw);
  main.variable(observer("formator")).define("formator", ["compute_ratios","count_refs","count_decls"], _formator);
  main.variable(observer("additional_results")).define("additional_results", ["additional_results_text","FA"], _additional_results);
  main.variable(observer("compute_ratios")).define("compute_ratios", _compute_ratios);
  main.variable(observer("count_refs")).define("count_refs", _count_refs);
  main.variable(observer("count_decls")).define("count_decls", _count_decls);
  main.variable(observer("seedrandom")).define("seedrandom", ["require"], _seedrandom);
  main.variable(observer("FA")).define("FA", ["FileAttachment"], _FA);
  const child1 = runtime.module(define1);
  main.import("Plot", child1);
  return main;
}
