console.log("This is console log")

const toggleSidebar=() => {
	if($(".sidebar").is(":visible")){
		$(".sidebar").css("display","none");
		$(".content").css("margin-left","0%");
	}
	else{
		$(".sidebar").css("display","block");
		$(".content").css("margin-left","20%");
	}
};

const searchContacts=()=> {
	let query = $("#search-input").val();
	
	if(query==''){
		$(".search-result").hide();
	}
	else{
		$(".search-result").show();
		
		let url = `http://localhost:8080/search/${query}`;
		
		fetch(url)
		.then((response)=>{
			return response.json();
		})
		.then((data)=>{
			console.log(data);
			
			let text = `<div class='list-group'>`;
			let count = 0;
			data.forEach((contact)=>{
				text+=`<a href='/user/${contact.cid}/contact-details' class='list-group-item list-group-action'> ${contact.name} </a>`;
				count++;
			});
			text+=`</div>`;
			
			if(count>0){
				$(".search-result").html(text);
			}
			else{
				//$(".search-result").hide();
				$(".search-result").html(`<p class='list-group-item list-group-action'>No such item found</p>`);
			}
		});
		
		//console.log(query);
	}
}