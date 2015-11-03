var LKFPlugin= function() {
};

LKFPlugin.prototype.get = function(success, fail) {
 				//alert(""+ts+"|"+nl+"|"+X0+"|"+Y0+"|"+Vel0+"|"+Ang0+"|"+NowX+"|"+NowY);
		
		 PhoneGap.exec(success, success, 'LKFPlugin', null, [ts,nl,X0,Y0,Vel0,Ang0,NowX,NowY,reNew] );
		 if(running==30){reNew=0;running=0;}
		 
};


cordova.addConstructor(function() {

	if (!window.plugins) {
		window.plugins = {};
	}
	window.plugins.LKFPlugin = new LKFPlugin();
});

 
