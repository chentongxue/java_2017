


var r;
var p;
var nav;
var myMarker;
var markSize=70;
var pHeading; 
var myName;  
var myPos;

 

var navStart= function () {


        r=Raphael("navHolder", 1340,622);//973
		g=r.image("data/f5.jpg",0, 0, 1340,622);//973
  
       // p = r.path('M0,600V0h200v600H0z');
	   
	    // = r.path('M100,0v600'); 100-markSize/2   -markSize/2 110,94
		 
	     myMarker = r.image("data/m.png",-35, -35, markSize, markSize) ;
		// myName=r.text(100 , markSize+5,"Malloo").attr({stroke:"#69c","font-size": 15,"font-family": "Arial, Helvetica, sans-serif" });
	 
	// myPos = r.set();
	 //myPos.push(myMarker, myName);

	     
	   /*
	   myMarker = r.ellipse(100,0, 7, 7).attr({
  stroke: "none", 
  opacity: .7, 
  fill: "#f00"
}); */

//animation = window.setInterval("animate()", 10);  //execute the animation function all 10ms (change the value for another speed)

 

};


 
var counter = 0;    // a counter that counts animation steps
function animate(poiX,poiY){
	//alert("animate="+poiX);
    /*
	if(nav.getTotalLength() <= counter){   //break as soon as the total length is reached
        clearInterval(animation);
        return;
    }*/
    //var pos = nav.getPointAtLength(counter);   //get the position (see Raphael docs)
    //myMarker.attr({cx: pos.x, cy: pos.y});  //set the circle position
	 
	//myMarker.attr({x: poiX, y: poiY}).animate(5000,">");  
	//myMarker.attr({x: poiX, y: poiY}).attr({transform: "r" + pHeading});
	//myMarker.attr({x: poiX-markSize/2, y: poiY-markSize/2}).animate({transform: "r" + pHeading});
	myMarker.animate({x: poiX, y: poiY},1000);
	
//myMarker.animate({x: poiX, y: poiY}, 1000).attr({transform: "r" + pHeading}) ; 
	//myMarker.attr({x: poiX, y: poiY});
	//alert(poiX+"||"+poiY);
    
    // counter++; count the step counter one up
};



function orientationChange(e) { 
             var orientation = "portrait";              
			 if (window.orientation == -90 || window.orientation == 90) orientation = "landscape";              
			 //document.getElementById("orientation").innerHTML = orientation; 
//r.remove();
//navStart();        
		             }  