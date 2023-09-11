/**
*
* Written by Christophe Trefois (trefex@gmail.com) for TCOM @ EPFL
* Release Date: August 16, 2007
*
*/

$(document).ready(function() {
	
	// Disable All Buttons
	$('input.item_modifier').attr("disabled","disabled");
	$('input.button-standard').attr("disabled","disabled");
	
	// Refresh the window to recover the current Session, if any
	initWindow();
	
	/**
	* Click on "Refresh" refreshes the window
	*/		
	$("#refresh_a").click(function() {
		initWindow();			
	});
	
	$('form.formNewLink').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("#loading").show();
	},
		success: function(msg) {
			$("div.errorBox").empty();
			if($("status", msg).text() == "2") {
					$("div.errorBox").html($(msg).find('errorMsg').text());
					$("#loading").hide();
					reloadPage($(msg).find('errorMsg').text());
					return;
				} else { 				
					$("div.errorBox").empty();
				$('div.mainContent').append("<br>Link Added: <br />Node " + $(msg).find('origin').text() + " -> Node " + $(msg).find('destination').text()).fadeIn("slow");
				$("#loading").hide();
				initWindow();
			}
		}
	});
	

	/**
	 
	*/
	
	function reloadPage(msg) {
	if (msg == "SESSION EXPIRED") {document.location.reload();}
	return false;
}
	
	
	
	$("input.item_modifier").click(function() {
		//console.log("Clicked: " + $(this).attr("name"));
		$.ajax({
			type: "GET",
			url: "action?DO=" + $(this).attr("name"),
			success: function(data) {
			    $("div.errorBox").empty();
				if($("status", data).text() == "2") {
				$("div.errorBox").html($(data).find('errorMsg').text());
				$("#loading").hide();
				reloadPage($(data).find('errorMsg').text());
				return;
			} else {
				$("#loading").hide();
				initWindow();
			}
		}});
	});
	
	/////////
	// Functions for Smooth Animations of the main Buttons - Create New Graph - Upload File - Execute Groovy
	/////////
	
	$("input.buttonAdd").click(function(){
			if($(this).val() == "Hide This Form") {
				$("div.right").slideUp(800);
				$(this).val("Upload New File");
			} else {
				$('#form').show();
				$("div.right").slideDown(800);	
				$(this).val("Hide This Form");
			}
	});
	
	$("input.buttonNew").click(function() {
			if($(this).val() == "Hide This Form") {
				$("div.newGraph").slideUp(800);
				$(this).val("Create New Graph");
			} else {
				$('#formName').show();
				$("div.newGraph").slideDown(800);	
				$(this).val("Hide This Form");
			}
	});
	
	$("input.buttonGroovy").click(function() {
			if($(this).val() == "Hide This Form") {
				$("div.executeGroovy").slideUp(800);
				$(this).val("Execute Groovy");
			} else {
				$('#execForm').show();
				$("div.executeGroovy").slideDown(800);	
				$(this).val("Hide This Form");
			}
	});
	
	// New Node 
	///////////
	
	$("input.buttonNode").click(function() {
		$("div.newNode").slideUp();
	});
	
	function addNode_New() {
		$('div.newNode').slideDown();
	}

	// Remove Node
	//////////////
	
	$("input.buttonRemNode").click(function() {
		$("div.remNode").slideUp();
	});
	
	function remNode() {
		$('div.remNode').slideDown();
	}
	
	$('#remNodeForm').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("#loading").show();
	},
		success: function(data) {
			$("div.errorBox").empty();
			if($("status", data).text() == "2") {
				$("div.errorBox").html($(data).find('errorMsg').text());
				$("#loading").hide();
				return;
			} else {
				$("div.mainContent").empty();
				$("div.errorBox").empty();
				$('div.mainContent').append("<br />Node Deleted");
				$("#loading").hide();
				initWindow();
			}
		}
	});
	
	// Add Link
	///////////
	
	$("input.buttonAddLink").click(function() {
		$("div.addLink").slideUp();
	});
	
	function addLink_New() {
		$('div.addLink').slideDown();
	}
	
	
		
	// Remove Link
	//////////////
	
	$("input.buttonRemLink").click(function() {
		$("div.remLink").slideUp();
	});
	
	function remLink() {
		$('div.remLink').slideDown();
	}
	
	$('#formDelLink').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("#loading").show();
	},
		success: function(msg) {
			$("div.errorBox").empty();
				if($("status", msg).text() == "2") {
						$("div.errorBox").html($(msg).find('errorMsg').text());
						$("#loading").hide();
						return;
					} else { 				
						$("div.errorBox").empty();
					$('div.mainContent').append("<br>Link Removed");
					$("#loading").hide();
				}
		}
	});
	
	// Obsolete
	function addNode() {
		$.ajax({
			type: "GET",
			url: "action?DO=NEWNODE",
			data: "p1=20&p2=30",
			success: function(msg) {
				$("div.errorBox").empty();
				$('div.mainContent').append("<br />Node Added: <br />X: " + $(msg).find('x').text() + "<br />Y: " + $(msg).find('y').text()).fadeIn("slow");
			}
		});
	}
	
	// Obsolete
	function addLink() {
		$.ajax({
			type: "GET",
			url: "action?DO=NEWLINK",
			data: "p1=0&p2=1",
			success: function(msg) {
				$("div.errorBox").empty();
				if($("status", msg).text() == "2") {
						$("div.errorBox").html($(msg).find('errorMsg').text());
						$("#loading").hide();
						return;
					} else { 				
					$('div.mainContent').append("<br>Link Added: <br />Node " + $(msg).find('origin').text() + " -> Node " + $(msg).find('destination').text()).fadeIn("slow");
				}
			}
		});
	}
	
	// File Upload 
	//////////////
	
	$('#form').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("div.right").find("p").html('Submitting...');
		$("#loading").show();
	},
		success: function(data) {
			var $out = $("div.right").find("p");
			$("div.errorBox").empty();
			if($("status", data).text() == "2") {
				$("div.errorBox").html($(data).find('errorMsg').text());
				$("#loading").hide();
				$('#form').fadeOut(2000);
				$out.empty();
				return;
			} else {
				$("div.mainContent").empty();
				var $out = $("div.right").find("p");
				$('#form').fadeOut(2000);
				$out.empty();
				$out.append("File Upload Succesful - FileName: " + $(data).find('filename').text());
				$out.append("&nbsp;- FileSize: " + $(data).find('filesize').text() + " [Bytes]");
				//$("div.fileInfo").html("Working on: <br>" + $(data).find('filename').text() );
				initWindow();
				$("#loading").hide();
			}
		}
	});
	
	// Graph Creation
	/////////////////
	
	$('#formName').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("div.newGraph").find("p").html('Creating...');
		$("#loading").show();
	},
		success: function(data) {
			var $out = $("div.newGraph").find("p");
			$('#formName').fadeOut(2000);
			$out.empty();
			$("div.mainContent").empty();
			$out.append("Graph Created with FileName: " + $(data).find('graphName').text());
			$("div.fileInfo").html("Working on: <br>" + $(data).find('graphName').text() );
			$("div.fileInfo").append("<br>Session ID: <br>" + $(data).find('sessionID').text() );
			$("#loading").hide();
			//$('#menuItems').slideDown(2000);
			initWindow();
		}
	});
	
	// Groovy Statements 
	////////////////////
	
	$('form.execForm').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("div.executeGroovy").find("p").html('Uploading...');
		$("#loading").show();
	},
		success: function(data) {
				var $out = $("div.executeGroovy").find("p");
				if($("status", data).text() == "2") {
					$("div.errorBox").html($(data).find('errorMsg').text());
					$("#loading").hide();
				//	$('#execForm').fadeOut(2000);
					$out.empty();
					return;
				} else {
				//	$('#execForm').fadeOut(2000);
					$("div.errorBox").empty();
					$out.empty();
					$out.html("Groovy Statement Executed");
					$("#loading").hide();
					initWindow();
				}
			}
	});
	
	
	

	
	
$('form.formNode').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("#loading").show();
	}, success: function(data) {
		$("div.errorBox").empty();
		if($("status", data).text() == "2") {
			$("div.errorBox").html($(data).find('errorMsg').text());
			$("#loading").hide();
			reloadPage($(data).find('errorMsg').text());
			return;
		} else {
			$("div.mainContent").empty();
			$("div.errorBox").empty();
			$('div.mainContent').append("<br />Node Added: <br />X: " + $(data).find('x').text() + "<br />Y: " + $(data).find('y').text()).fadeIn("slow");
			$("#loading").hide();
			initWindow();
		}
	}
});


function createGenericFormAjax() {
	$('form.genericForm').ajaxForm({
		beforeSubmit: function(a,f,o) {
		o.dataType = 'xml';
		$("#loading").show();
		}, success: function(msg) {
			$("div.errorBox").empty();
			if($("status", msg).text() == "2") {
				$("div.errorBox").html($(msg).find('errorMsg').text());
				$("#loading").hide();
				return;
			} else {
				$("div.mainContent").empty();
				$("div.errorBox").empty();
				$("#loading").hide();
				$('div.element_container_attr_list').empty();
	
				var id_xml = "";
				if($(msg).find('node').size() > 0) {
					id_xml = "N_" + $(msg).find('node').attr("id");
					$s = "<b>Attributes for Node:</b> <i/>" + $(msg).find('node').attr("id");
				}
				if($(msg).find('link').size() > 0) {
					id_xml = "L_" + $(msg).find('link').attr("orig") + "," + $(msg).find('link').attr("dest") + "_" + $(msg).find('link').attr("on_layer");
					$s = "<b>Attributes for Link: </b><i>" + $(msg).find('link').attr("orig") + "</i><b> -> </b><i>" + $(msg).find('link').attr("dest") + "</i> <b>on Layer: </b><i/>" + $(msg).find('link').attr("on_layer");
				}
				$s = $s + "&nbsp;&nbsp; <input type=button name=REM_ELEMENT id=remEl class=\"button-standard\" key=\"" + id_xml + "\" value=Remove />";
				$('div.element_container_attr_list').append($s + "<br /><br />");
				
				$c_temp = 0;
				$("node/attribute", msg).each(function($i) {
					// alert($(this).attr("name") + " - " + $(this).attr("value"));
					// Foreach Attribute DO THIS
					
					$s = '<div class="attribute_list">' + $(this).attr("name") + '</div>';
					$s = $s + '<div class="editme" id="Edit_' + $i +'" attr_name="' + $(this).attr("name") + '" el_id="' + id_xml + '">' + $(this).attr("value") + '</div>';
					//$s = $s + '<div style="margin-left: 10px;float:left;width:80px;border: 0px solid red;">&nbsp;</div>';
					//$s = $s + "<br><br><br>";
					$('div.element_container_attr_list').append($s);
					$("#Edit_" + $i).editInPlace({
						callback: function(original_element, html){
							var mod_id = $('#Edit_' + $i);
							//console.log(mod_id.attr("attr_name"));
							//console.log(mod_id.attr("el_id"));
								//console.log(html);													
							//MODIFY AJAX
								$.ajax({
								type: "GET",
								url: "action?DO=SET_ATTRIBUTE",
								data: "id=" + mod_id.attr("el_id") + "&attName=" + mod_id.attr("attr_name") + "&attValue=" + html,
								success: function(msg) {
									$("div.errorBox").empty();
									$c_temp = $c_temp + 1;
									initWindow();
								}
							});
								return(html);
						}
					});
					// END FOREACH
				});
				
				$("link/attribute", msg).each(function($i) {
					// alert($(this).attr("name") + " - " + $(this).attr("value"));
					// Foreach Attribute DO THIS
				//	var id_xml = "";
					
					$s = '<div class="attribute_list">' + $(this).attr("name") + '</div>';
					$s = $s + '<div class="editme" id="Edit_' + $i +'" attr_name="' + $(this).attr("name") + '" el_id="' + id_xml + '">' + $(this).attr("value") + '</div>';
					
					$('div.element_container_attr_list').append($s);
					$("#Edit_" + $i).editInPlace({
						callback: function(original_element, html){
							var mod_id = $('#Edit_' + $i);
							//console.log(mod_id.attr("attr_name"));
							//console.log(mod_id.attr("el_id"));
								//console.log(html);													
							//MODIFY AJAX
								$.ajax({
								type: "GET",
								url: "action?DO=SET_ATTRIBUTE",
								data: "id=" + mod_id.attr("el_id") + "&attName=" + mod_id.attr("attr_name") + "&attValue=" + html,
								success: function(msg) {
									$("div.errorBox").empty();
									$c_temp = $c_temp + 1;
									initWindow();
								}
							});
								return(html);
						}
					});
				});
				$s = '<div class="att_add">&nbsp;<form method="post" action="action?DO=SET_ATTRIBUTE" class="genericForm"><fieldset><legend>Add Atrribute</legend>\
				<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input type="text" class=attr_input value="Attr_Name" name="attName" onfocus="this.value=\'\';" /></div>';
				$s = $s + '<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input class="attr_input" type="hidden" value="' + id_xml + '" name="id" onfocus="this.value=\'\';" /></div>';
				$s = $s + '<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input class="attr_input" type="text" width=10 value="Attr_Value" name="attValue" onfocus="this.value=\'\';" /></div>';
				$s = $s + '<input type="submit" class="button-standard" value="Add Attrib"/></fieldset></form></div>';
				$('div.element_container_attr_list').append($s);
				createGenericFormAjax();
				addRemElListener();
				initWindow();
			}
		}
	});
};

// $s = $s + "&nbsp;&nbsp; <input type=button name=REM_ELEMENT key= id=remEl value=Remove />";
/**
* Adds an Ajax action to the Remove Button
*
*/

function addRemElListener() {
	$("#remEl").click(function() {
		$("#loading").show();
		$.ajax({
			type: "GET",
			url: "action?DO=" + $(this).attr("name"),
			data: "key=" + $(this).attr("key"),
			success: function(data) {
				$("div.errorBox").empty();
				if($("status", data).text() == "2") {
					$("div.errorBox").html($(data).find('errorMsg').text());
					$("#loading").hide();
					return;
				} else {
					$("div.mainContent").empty();
					$("div.errorBox").empty();
					$("#loading").hide();
					$('div.element_container_attr_list').empty();
				}
				initWindow();
			}
		});
	});
};


$init_counter = 0;
	
// Function used to refresh the whole network 
//////////////////////////////////////////////

function initWindow() {
	$("#loading").show();
	$.ajax({
		type: "GET",
		url: "action?DO=RECOVERSESSION",
		success: function(data) {
			if($("status", data).text() == "2" || $("additional_description/network", data).attr("graph_name") == "") {
				$("div.errorBox").html($(data).find('errorMsg').text());
				$("div.fileInfo").html("New User");
				$.ajax({
					type: "GET",
					url: "images/graph.png?map=1",
					success: function(data1) {
						$("#image_cont").empty().append(data1);
					}});
				$("#loading").hide();
				return;
			} else { 				
 				$("div.fileInfo").html("<i>Working on</i> <br>" + $("additional_description/network", data).attr("graph_name"));
				$("div.fileInfo").append("<br/><i>Session ID</i> <br>" + $(data).find('sessionID').text() );
				$("#loading").hide();
				//$('#menuItems').slideDown(2000);
				//$init_counter = $init_counter + 1;
				//$("#image_container").attr("src","images/graph" + $init_counter + ".png");
				//$('input.item_modifier').disabled = true;
				$.ajax({
					type: "GET",
					url: "images/graph.png?map=1",
					success: function(data1) {
						$("#image_cont").empty().append(data1);
						
						//**************************
						//
						// WE CLICK ON A MAP ITEM
						//
						//**************************
						
						$("area.AbstractElementContainer").click(function() {
							//console.log($(this).attr("shape") + " - " + $(this).attr("id"));
							// Ajax get Attribute XML
							$.ajax({
								type: "GET",
								url: "action?DO=SELECT",
								data: "id=" + $(this).attr("id"),
								success: function(msg) {
									$("div.errorBox").empty();
									$('div.element_container_attr_list').empty();

									var id_xml = "";
									if($(msg).find('node').size() > 0) {
										id_xml = "N_" + $(msg).find('node').attr("id");
										$s = "<b>Attributes for Node:</b> <i/>" + $(msg).find('node').attr("id");
									}
									if($(msg).find('link').size() > 0) {
										id_xml = "L_" + $(msg).find('link').attr("orig") + "," + $(msg).find('link').attr("dest") + "_" + $(msg).find('link').attr("on_layer");
										$s = "<b>Attributes for Link: </b><i>" + $(msg).find('link').attr("orig") + "</i><b> -> </b><i>" + $(msg).find('link').attr("dest") + "</i> <b>on Layer: </b><i/>" + $(msg).find('link').attr("on_layer");
									}
									$s = $s + "&nbsp;&nbsp; <input type=button name=REM_ELEMENT key=\"" + id_xml + "\" class=\"button-standard\" id=remEl value=Remove />";
									$('div.element_container_attr_list').append($s + "<br /><br />");
									
									$c_temp = 0;
									$("node/attribute", msg).each(function($i) {
										// alert($(this).attr("name") + " - " + $(this).attr("value"));
										// Foreach Attribute DO THIS
										
										$s = '<div class="attribute_list">' + $(this).attr("name") + '</div>';
			    						$s = $s + '<div class="editme" id="Edit_' + $i +'" attr_name="' + $(this).attr("name") + '" el_id="' + id_xml + '">' + $(this).attr("value") + '</div>';
			    						//$s = $s + '<div style="margin-left: 10px;float:left;width:80px;border: 0px solid red;">&nbsp;</div>';
			    						//$s = $s + "<br><br><br>";
										$('div.element_container_attr_list').append($s);
										$("#Edit_" + $i).editInPlace({
											callback: function(original_element, html){
												var mod_id = $('#Edit_' + $i);
												//console.log(mod_id.attr("attr_name"));
												//console.log(mod_id.attr("el_id"));
		  										//console.log(html);													
												//MODIFY AJAX
		 										$.ajax({
													type: "GET",
													url: "action?DO=SET_ATTRIBUTE",
													data: "id=" + mod_id.attr("el_id") + "&attName=" + mod_id.attr("attr_name") + "&attValue=" + html,
													success: function(msg) {
														$("div.errorBox").empty();
														$c_temp = $c_temp + 1;
														initWindow();
													}
												});
		  										return(html);
											}
										});
										// END FOREACH
									});
									
									$("link/attribute", msg).each(function($i) {
										// alert($(this).attr("name") + " - " + $(this).attr("value"));
										// Foreach Attribute DO THIS
									//	var id_xml = "";
										
										$s = '<div class="attribute_list">' + $(this).attr("name") + '</div>';
			    						$s = $s + '<div class="editme" id="Edit_' + $i +'" attr_name="' + $(this).attr("name") + '" el_id="' + id_xml + '">' + $(this).attr("value") + '</div>';
			    						
										$('div.element_container_attr_list').append($s);
										$("#Edit_" + $i).editInPlace({
											callback: function(original_element, html){
												var mod_id = $('#Edit_' + $i);
												//console.log(mod_id.attr("attr_name"));
												//console.log(mod_id.attr("el_id"));
		  										//console.log(html);													
												//MODIFY AJAX
		 										$.ajax({
													type: "GET",
													url: "action?DO=SET_ATTRIBUTE",
													data: "id=" + mod_id.attr("el_id") + "&attName=" + mod_id.attr("attr_name") + "&attValue=" + html,
													success: function(msg) {
														$("div.errorBox").empty();
														$c_temp = $c_temp + 1;
														initWindow();
													}
												});
		  										return(html);
											}
										});
									});
									$s = '<div class="att_add">&nbsp;<form method="post" action="action?DO=SET_ATTRIBUTE" class="genericForm"><fieldset><legend>Add Atrribute</legend>\
									<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input type="text" class=attr_input value="Attr_Name" name="attName" onfocus="this.value=\'\';" /></div>';
									$s = $s + '<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input class="attr_input" type="hidden" value="' + id_xml + '" name="id" onfocus="this.value=\'\';" /></div>';
									$s = $s + '<div style="margin-left:10px;float:left;width:125px;border:0px solid red;"><input class="attr_input" type="text" width=10 value="Attr_Value" name="attValue" onfocus="this.value=\'\';" /></div>';
									$s = $s + '<input type="submit" class="button-standard" value="Add Attrib"/></fieldset></form></div>';
									$('div.element_container_attr_list').append($s);
									createGenericFormAjax();
									addRemElListener();
								}
							});	
						});
				}});
				$('input.item_modifier').attr("disabled","");
				$('input.button-standard').attr("disabled","");
			}
			$("div.mainContent").empty();
			// Parse the XML structure for node elements
			$("layer/node", data).each(function() {
				$("div.mainContent").append("Node " + ($(this).attr("id") + " - X: " + $(this).attr("pos_x") + " Y: " + $(this).attr("pos_y")) + "<br/>");
			});
			$("div.mainContent").append("<br />");
					
			$("layer", data).each(function() {
				if(($(this).find('node').size()) == 0 || ($(this).find('link').size()) > 0) {
					$("div.mainContent").append("Layer: <b>" + $(this).attr("id") + "</b><br/><br/>");
				}
				var $i = 0;
				$("link", $(this)).each(function() {
					$("div.mainContent").append(($(this).attr("orig") + " -> " + $(this).attr("dest") + "<br/>"));
					$i++;
				});
				$("div.mainContent").append("<br/>");
			});
		},
		error: function(AjaxReq, errorData, optional) {
			alert(errorData);
		}
	});
}

});