import define1 from "./c4134a8cb65e3e72@465.js";

function _1(md){return(
md`# Construction Scaling`
)}

function _article_results(Inputs){return(
Inputs.toggle({label: "Show results presented in article", value: true})
)}

function _3(md){return(
md`Rendering the plots might take some time (less than a minute) and memory, you browser might ask you to abort but it should end up rendering if you wait a bit.

You can visualize results that you computed with the reproduction package, by adding the path to the Json containing performance results (one file per line). For example, using \`run_benchmark_simp.sh\` you should write \`my_spoon results_simp/perfs_spoon.json\`. As another example, using \`run_benchmark_all.sh\` you should write \`my_spoon results/perfs_spoon.json\`.

If you want to test more repositories and commits, you should either modify variables (SHORTNAME,REPO,BEFORE,AFTER) in  \`run_benchmark_simp.sh\` or modify entries in \`hyperAST/all.csv\` and run \`run_benchmark_all.sh\`.`
)}

function _additional_results_text(Inputs){return(
Inputs.textarea({label: "Result computed with reproduction package", submit: true})
)}

function _additional_projects_name(additional_results){return(
additional_results.map(x=>x.project)
)}

function _construction_mem_footprint(perfs,projects_name,projects_by_size,Plot,reg,d3,article_results,additional_projects_name,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = perfs.filter(x=> !projects_name.includes(x.project) || projects_by_size.includes(x.project)).map(d=> ({...d, type: (f_type[d.type]) }));
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
      strokeWidth: 2,
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
    domain: [...(article_results?projects_by_size:[])
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             
             ,...additional_projects_name],
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


function _construction_time(perfs,projects_name,projects_by_size,Plot,reg,d3,article_results,additional_projects_name,scale)
{
  let f_type = {baseline:"spoon",evaluation:"hyperAST"};
  let d_plus = perfs.filter(x=> !projects_name.includes(x.project) || projects_by_size.includes(x.project)).map(d=> ({...d, type: (f_type[d.type]) }));
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
      strokeWidth: 2,
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
    domain: [...(article_results?projects_by_size:[]),...additional_projects_name],
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


function _scale(Inputs){return(
Inputs.select(['linear','log'], { label: 'Scale', value: 'linear' })
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

function _12(md){return(
md`## Appendix`
)}

function _max_decls(d3,perfs){return(
new Map(Array.from(d3.group(perfs, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        // .filter(x=>x.type==="evaluation")
                                        // .map(x=>x.matched_decl_count)
                                        .map(x=>x.decl_count)
                                       )]))
)}

function _max_matched_decls(d3,perfs){return(
new Map(Array.from(d3.group(perfs, d => d.project),
                   ([k,v])=>[k,Math.max(...v
                                        // .filter(x=>x.type==="evaluation")
                                        // .map(x=>x.matched_decl_count)
                                        .map(x=>x.matched_decl_count)
                                       )]))
)}

function _projects_name(perfs_raw){return(
perfs_raw.map(x=>x.project)
)}

function _perfs_raw(article_results,FileAttachment){return(
article_results ? (async () => [
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
])() : []
)}

function _perfs(perfs_raw,additional_results,d3){return(
[...perfs_raw,...additional_results].map(xx=> xx.content.sort(function(x, y){
   return d3.ascending(x.info.no, y.info.no);
}).map(x => ({
  project:xx.project,
  type:x.processor,
  construction_time:x.construction_perfs.time/1000/1000/1000,
  construction_memory_fooprint:x.construction_perfs.memory/1000/1000,
  no:x.info.no,
  commit:x.info.commit,
}))).flat()
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
    ["perfs_junit4.json", {url: new URL("./files/c8199e94110819c6f1317585cfe8dccad67fb31744b4700674c40bf2a6f93ce3746a8bce517504fb5ad0a500e305d4e3b6f092fb699faaac120e48361d37effc.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_javaparser.json", {url: new URL("./files/0bf1cbc759876ebf4b798cfddfdcf3ce7cb2a9bab0a38b88994acae68aebd49a98a34ae431ac085b8c057ccfc61dbaa5fed12b72ba1fce2cbcc061fc055114a9.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_spoon.json", {url: new URL("./files/fc3514afd580b0b0c4ef204915b6ab1f541995787d0686796fba7dd6450357ea631b596cd1af37d223cc02d091e40917d1ee6c87b0714794dbdbf733249a86e2.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_quarkus.json", {url: new URL("./files/e4fec22552657ef54f2ee1a8a5049e8fdc88634491fc246d6f1f37e0a7b691e2b88c5f893be8a4a7db1ab0b46b93f21c363082397ebe92c519710923cbda525b.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_flink.json", {url: new URL("./files/58a1ae11fc3b3e0a06ab0209eab45666932efedfc61e3a16b3921f2e9872bb85e0ec34e83f0c1db09d104b9c9b03da901ec6e7ff9780d7f4eb379e7552ea46e1.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_skywalking.json", {url: new URL("./files/bfbc23bc0178243ea47fa4b92a6af8d80d1e657116055967bbbe4489808bb5f0df9e32348c63797311f2a203eb7c341819964044c2a771822632f934eef97831.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_gson@1.json", {url: new URL("./files/a53605c625d072fb236a8f69af9ba36872e041e837a15a059ffa7481353ffbb8a7a9f34e88750de2938afa698baaa5eb1acc9dd8f704b0ab0f726176dc7f2bae.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_arthas@2.json", {url: new URL("./files/11a53708225bc5a0e0ce7cf572b4530ba483084469090cda247cb92030ef33840d53073bff9ec80e9246a4472624f174c66e479c793005a784acf615cf065837.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_hadoop@1.json", {url: new URL("./files/e523d8dfe30b1d613280d12badc2ec1affc1c7f8e0672a68f5a769ebc75fca6df63dbd1f67d7c690dace7d8574c895aeacef7abf5959998d86ea356f3f160c23.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_spark.json", {url: new URL("./files/67e5aacb93961981cbd993b3d0c733c69ffc654abff471ae91c23fb8d9fed780cf983f8c2e3d155e809c1266b1eacf09b7f8a80c99fb7562730fda18c7d838c9.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_aws-toolkit-eclipse@1.json", {url: new URL("./files/7045caa7ae82eeed1e075fa9f0a2b1cbcaf7e0444fb6f399f74337f144696fcec42cd8757413a6299f4e391f913f5a114acf88843cf59170fa6d88c70eba05c0.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_dubbo.json", {url: new URL("./files/1b3ffab96169ac9a8476d2aa35c07977b1365fc6b6efd92a38cd0103a43a7ddaf3d6bb80ea6c8d7d169ab1b57b786dc956cf08d8fca6aa6eb35d98e448988fa9.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_aws-sdk-java-v2@1.json", {url: new URL("./files/1c59e257eea7b263e6986ae2ecf52cf5c2dd3f18493e46568e79266fe2fecf116f5c3d1be16160c9dc759d2097c3c8f5061321b244a474f75b190d880f949d10.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_netty.json", {url: new URL("./files/8e46207a52a36664ac817f4795027dc3b72dad42515dee0232eebb9ef25bfc47a9c3dc46d55e2940eddd23f1638d4b88e7ea8e9b61b3c8cc2092d61da51c10af.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jackson-core@1.json", {url: new URL("./files/e9153f11dd7b619be91bd0134036c41b31a2677e7843b08ad9b64c9d4b3a3489b6e0846c395826f2f3a4eaa9b05616514e092ae98a4f9334f0d00fa466161d27.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_fastjson@4.json", {url: new URL("./files/6a517d29fc95ff388f4d6c79b08b92d82e7582ead411f73090473617c18ce85007615b15863e250313ed07e948459fa2546f6a62bb85b62e30b5422cdfa77bac.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_guava.json", {url: new URL("./files/b4aa1f11c69ba40c890f42f1f23597c3549dc0cadc1010821fd8dde712a9c713338c64c7d4dc5ce07f5a2ef438b369f291f8443905debfa1620303a667c216e8.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jacoco.json", {url: new URL("./files/cc9852de6934f49e78004c87348c2e4b88f2110369783fdc367664ee616a70e1ced57dd3168eb6a05d24353e296fce97c84b24b38597b3069d4af0d85f01891f.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_slf4j.json", {url: new URL("./files/54b7367d788b5c3d72aaa1a468c4c57c3c9fec620dffc846ac21a8211aeed26721980bf71c7803ff3d7cc3cd34496f32ede74e8f1c5c9a7fd1ef7403d85e3839.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_logging-log4j2.json", {url: new URL("./files/873498e24725a557233482b75e512948d97d4beccc2d68620d3ec0b449e80cab723b0e32ab17079c53ddd71bd2e4c8aae8f9aa4eb12a088c9550d0e80dc5d286.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_maven.json", {url: new URL("./files/9ea3ace508570b48f41042ac40455543c3f41acbf10ae5163e5e8778539c8fbb6052fb33c93d3519c431762187b1ce8a8a5ff4ef649b3110bcbb97941861d140.json", import.meta.url), mimeType: "application/json", toString}],
    ["perfs_jenkins.json", {url: new URL("./files/430dc8c7ac8ba3d84326d21038a98971515ba62952894b45bb4088a2a52916e26716e8d854619f900ce8865fb157ce4a548188dfc5aef75cf860b5e79fe41e78.json", import.meta.url), mimeType: "application/json", toString}]
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
  main.variable(observer("construction_mem_footprint")).define("construction_mem_footprint", ["perfs","projects_name","projects_by_size","Plot","reg","d3","article_results","additional_projects_name","scale"], _construction_mem_footprint);
  main.variable(observer("construction_time")).define("construction_time", ["perfs","projects_name","projects_by_size","Plot","reg","d3","article_results","additional_projects_name","scale"], _construction_time);
  main.variable(observer("viewof scale")).define("viewof scale", ["Inputs"], _scale);
  main.variable(observer("scale")).define("scale", ["Generators", "viewof scale"], (G, _) => G.input(_));
  main.variable(observer("reg")).define("reg", ["require"], _reg);
  main.variable(observer("projects_scale")).define("projects_scale", ["d3","projects_by_size"], _projects_scale);
  main.variable(observer("projects_by_size")).define("projects_by_size", _projects_by_size);
  main.variable(observer()).define(["md"], _12);
  main.variable(observer("max_decls")).define("max_decls", ["d3","perfs"], _max_decls);
  main.variable(observer("max_matched_decls")).define("max_matched_decls", ["d3","perfs"], _max_matched_decls);
  main.variable(observer("projects_name")).define("projects_name", ["perfs_raw"], _projects_name);
  main.variable(observer("perfs_raw")).define("perfs_raw", ["article_results","FileAttachment"], _perfs_raw);
  main.variable(observer("perfs")).define("perfs", ["perfs_raw","additional_results","d3"], _perfs);
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
