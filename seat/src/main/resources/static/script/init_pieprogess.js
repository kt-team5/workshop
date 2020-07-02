function start_pieprogress(){
    $('.pie_progress').asPieProgress({
        namespace: 'pie_progress'
      });
      // Example with grater loading time - loads longer
      $('.pie_progress--slow').asPieProgress({
        namespace: 'pie_progress',
        goal: 1000,
        min: 0,
        max: 1000,
        speed: 200,
        easing: 'linear'
      });
      // Example with grater loading time - loads longer
      $('.pie_progress--countdown').asPieProgress({
        namespace: 'pie_progress',
        easing: 'linear',
        first: 120,
        max: 120,
        goal: 0,
        speed: 1200, // 120 s * 1000 ms per s / 100
        numberCallback: function(n) {
          var minutes = Math.floor(this.now / 60);
          var seconds = this.now % 60;
          if (seconds < 10) {
            seconds = '0' + seconds;
          }
          return minutes + ': ' + seconds;
        }
      });
      $('#button_start').on('click', function() {
        $('.pie_progress').asPieProgress('start');
      });
      $('#button_finish').on('click', function() {
        $('.pie_progress').asPieProgress('finish');
      });
      $('#button_go').on('click', function() {
        $('.pie_progress').asPieProgress('go', 50);
      });
      $('#button_go_percentage').on('click', function() {
        $('.pie_progress').asPieProgress('go', '50%');
      });
      $('#button_stop').on('click', function() {
        $('.pie_progress').asPieProgress('stop');
      });
      $('#button_reset').on('click', function() {
        $('.pie_progress').asPieProgress('reset');
      });


      	
      $('.pie_button').on('click', function() {
    	  var qry = './setInfo?seatId='+$(this).attr('id')+'&usages='+$("#timeset" ).val();
    	  //var qry = './setInfo?seatId='+$(this).attr('id')+'&usages=60';
    	  //alert('==='+$("#timeset" ).val());
    	  //var qry = 'http://localhost:8086/setInfo?seatId='+$(this).attr('id')+'&usages=60';
    	  if($(this).text() == "예약") qry = qry +'&occupied=true'
    	  else qry = qry +'&occupied=false'
          $.get(qry,function(data){
        	  if(data != 'SUCCESS'){
        		  //alert('실패!');    	
        		  //$(this).css('background-color', 'lightgray');
        		  
        	  }
        	  else {
        		  //alert('성공!');
        		  //$(this).css('background-color', 'yellow');
        		  if($(this).text() == "예약")$(this).text('중단');
        		  else $(this).text('예약');
        	  }
        		  
          });
        });
      
      setInterval(function(){
        $.getJSON('./getInfo',function(data){
            $.each(data,function(key,val){
                $('#progress'+val.seatId).asPieProgress('go', val.usages);
                if(val.usages > 0)$('#'+val.seatId).text('중단');
                else $('#'+val.seatId).text('예약');
                //if(val.usages > 0)$('#'+val.seatId).css('background-color', 'yellow');
               // alert("startTime:"+converTime(val.startTime) +"seatId:"+val.seatId +" /usages:"+val.usages +" /occupied:"+val.occupied);
            })
	      });
	    },1000);
        
}


function converTime(timesrc){
	if(timesrc != undefined && timesrc != null)
		{
			var d = new Date(parseInt(timesrc.substr(6)));
			var month = d.getMonth() +1;
			var day = d.getDate();
			var hour = d.getHours();
			var miniute = d.getMiniutes();
			var second = d.getSeconds();
			
			var datetime = d.getFullYear() 
			+ '-' +month
			+ '-' +day
			+ ' ' +hour
			+ ':' +miniute
			+ ':' +second;
		}
	
}
