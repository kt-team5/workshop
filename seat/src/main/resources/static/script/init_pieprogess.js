function start_pieprogress(){
    $('.pie_progress').asPieProgress({
        namespace: 'pie_progress'
      });
      // Example with grater loading time - loads longer
      $('.pie_progress--slow').asPieProgress({
        namespace: 'pie_progress',
        goal: 100,
        min: 0,
        max: 100,
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
//      $('#button_start').on('click', function() {
//        $('.pie_progress').asPieProgress('start');
//      });
//      $('#button_finish').on('click', function() {
//        $('.pie_progress').asPieProgress('finish');
//      });
//      $('#button_go').on('click', function() {
//        $('.pie_progress').asPieProgress('go', 50);
//      });
//      $('#button_go_percentage').on('click', function() {
//        $('.pie_progress').asPieProgress('go', '50%');
//      });
//      $('#button_stop').on('click', function() {
//        $('.pie_progress').asPieProgress('stop');
//      });
//      $('#button_reset').on('click', function() {
//        $('.pie_progress').asPieProgress('reset');
//      });

      
     var btnnum=0; 	
      $('.pie_button').on('click', function() {
    	  if($.isNumeric($(".timesetform" ).val().trim()) == false){
    		  alert('숫자를 입력하시오.--->'+$(".timesetform" ).val().trim())
    		  $(".timesetform").focus();
    		  return;
    	  }
    	  else if($(".timesetform").val() > 999 || $(".timesetform").val() <= 0){
    		  alert('1~999 사이의 숫자를 입력하시오.--->'+$(".timesetform" ).val().trim())
    		  $(".timesetform").focus();
    		  return;
    	  }
    	  btnnum = $(this).attr('id');
    	  var qry = 'http://localhost:8081/setInfo?seatId='+btnnum+'&usages='+$(".timesetform").val();
    	  if($('#'+btnnum).text() == "예약") qry = qry +'&occupied=true';
    	  else qry = qry +'&occupied=false';
          $.get(qry,function(data){
        	  if(data != 'SUCCESS'){
        		  alert('실패!');    	
        	  }
        	  else {
        		  if($('#'+btnnum).text() == "예약")$('#'+btnnum).text('중단');
        		  else $('#'+btnnum).text('예약');
        		  $('#'+btnnum).addClass("color_yellow");
        		  setTimeout(function(){
        			  $('#'+btnnum).removeClass("color_yellow");
        		  },500);
        		  
        	  }
        		  
          });
        });
      
      var demo_act = 0;
      setInterval(function(){
    	if(demo_act++ % 5 == 0)
    		{
    			for(var i = 1;i <= 10;i++)$('#progress'+i).asPieProgress('go', 999);
    		}
    	else
    		{
		        $.getJSON('./getInfo',function(data){
		        	for(var i = 1;i <= 10;i++)$('#progress'+i).asPieProgress('go', 0);
		        	$('.pie_progress__content').text('0분');
		            $.each(data,function(key,val){
		                $('#progress'+val.seatId).asPieProgress('go', val.usages);
		                $('#progress'+val.seatId+'> .pie_progress__content').text(val.usages+'분');
		                if(val.usages > 0)$('#'+val.seatId).text('중단');
		                else $('#'+val.seatId).text('예약');
		            })
			      });
    		}
	    },1000);
      
      $('.snackbar > div').on('click', function() {
        alert('간식 주문하실건가요?');
      });
      $('.shoptitle_sub2').on('click', function() {
          alert('도움말 메뉴 클릭');
        });
      $('.shoptitle_sub3').on('click', function() {
    	  if($('.shoptitle_sub3').text() == "나의 화면")location.href = "mypage.html";
    	  else location.href = "index.html";
          //alert('나의메뉴');
        });
 
}




