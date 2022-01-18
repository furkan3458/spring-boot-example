$(document).ready(function(){
	var stompClient = connectScheduledSocketSubscribe();	
});

function connectScheduledSocketSubscribe() {
    var socket = new SockJS('/socket');
    var stomp = Stomp.over(socket);
    
    /* Comment this line when you want to show stomp detail debug informations.*/
    stomp.debug = null;
    
    stomp.connect({}, function (frame) {
        console.log('Connected: ' + frame);
 //       stomp.subscribe('/scheduled/subscribe', function (data) {
//            console.log(JSON.parse(data.body));
//        });
        stomp.subscribe('/notification/subscribe', function (data) {
            console.log(JSON.parse(data.body));
        });
        stomp.subscribe('/api/test/topic/greetings', function (greeting) {
        });
    });
    
    return stomp;
}