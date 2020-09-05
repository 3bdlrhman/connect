# connect
this is a console java application 
uses jSeialCom library to communicate with Temperature Controller
read specific temperature value and send it as a commands to the Controller
then it sends a commands to read from the Controller it's current value
to make sure the value we send is fixed by now
to do so i created a loop with a statement if the current temperature
equals the value we send then update collision value by one
once the collision is four there we make sure the value is fixed and we terminate the programe
