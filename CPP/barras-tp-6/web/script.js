var App = new Vue({
    el: '#app',
    data : {
        kmeansSeed : -1,
        kmeansPoints : 500,
        kmeansMin : 0,
        kmeansMax : 1000,
        kmeansClusers : 9,
        author :'??',
        algorithm: "kmeans"
    },

    created : function(){
        obj = this;
        axios.get('author')
            .then(function (response) {
                obj.author = response.data;
            })
            .catch(function (error) {
                console.log(error);
                obj.author = 'Error';
            });
        this.create_points();
    },

    methods : {
        update_clusters : function () {
            updateGraph("clusters", this.kmeansSeed, this.kmeansPoints, this.kmeansMin, this.kmeansMax, this.kmeansClusers, this.algorithm);
        },
        create_points : function (){
            updateGraph("points", this.kmeansSeed, this.kmeansPoints, this.kmeansMin, this.kmeansMax, this.kmeansClusers, this.algorithm);
        }
    }
})


//Source: https://www.d3-graph-gallery.com/graph/scatter_basic.html

// set the dimensions and margins of the graph
var margin = {top: 10, right: 30, bottom: 30, left: 60},
    width = 600 - margin.left - margin.right,
    height = 600 - margin.top - margin.bottom;

// append the svg object to the body of the page
var svg = d3.select("#my_dataviz")
    .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
        "translate(" + margin.left + "," + margin.top + ")");

//https://stackoverflow.com/questions/36721830/convert-hsl-to-rgb-and-hex/54014428#54014428
// input: h in [0,360] and s,v in [0,1] - output: r,g,b in [0,1]
function hsl2rgb(h,s,l)
{
    let a = s * Math.min(l,1-l);
    let f = (n,k=(n+h/30)%12) => l - a*Math.max(Math.min(k-3,9-k,1),-1);
    return [f(0),f(8),f(4)];
}

//https://stackoverflow.com/questions/5623838/rgb-to-hex-and-hex-to-rgb
function componentToHex(c) {
    var hex = c.toString(16);
    return hex.length == 1 ? "0" + hex : hex;
}

function rgbToHex(r, g, b) {
    return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
}

//https://martin.ankerl.com/2009/12/09/how-to-create-random-colors-programmatically/
function randomColor(seed) {
    // use golden ratio
    golden_ratio_conjugate = 0.618033988749895 * 360
    seed *= golden_ratio_conjugate
    seed %= 360

    rgb = hsl2rgb(seed, 0.8, 0.4)

    return rgbToHex(Math.floor(rgb[0] * 255), Math.floor(rgb[1] * 255), Math.floor(rgb[2] * 255))
}

function updateGraph(method, kmeansSeed, kmeansPoints, kmeansMin, kmeansMax, kmeansClusers, algorithm){
    //Read the data
    d3.csv("/"+method+"?seed=" + kmeansSeed + "&points=" + kmeansPoints+"&min="+kmeansMin+"&max="+kmeansMax+"&clusters="+kmeansClusers+"&algorithm="+algorithm, function(data) {
        //Clear
        svg.selectAll("*").remove();

        // Add X axis
        var x = d3.scaleLinear()
            .domain([kmeansMin, kmeansMax])
            .range([ 0, width ]);
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Add Y axis
        var y = d3.scaleLinear()
            .domain([kmeansMin, kmeansMax])
            .range([ height, 0]);
        svg.append("g")
            .call(d3.axisLeft(y));

        // Add dots
        svg.append('g')
            .selectAll("dot")
            .data(data)
            .enter()
            .append("circle")
            .attr("cx", function (d) { return x(d.x); } )
            .attr("cy", function (d) { return y(d.y); } )
            .attr("r", 3.5)
            .style("fill", function (d) {
                color = randomColor(d.cluster);
                return color;
            })

    })
}