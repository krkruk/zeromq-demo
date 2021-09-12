zeromq-demo
================================================================================

The aim of this demo is to present capabilities of ZeroMQ and its ease of use
in robotics environment.

There are 3 apps:

zeromq-server:
	* the application acts as an onboard broker that provides feedback system information as well as forwards commands further to microcontrollers if necessary

zeromq-client:
	* a simple client requesting telemetry data

zeromq-rover-inputs:
	* a simple input source such as an application that reads joystick data and forwards it to the onboard computer

::
	zeromq-server  ---  zeromq-client
				| 
				+  ---  zeromq-rover-inputs
