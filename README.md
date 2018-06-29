# NZTRADER
Generic Java-based framework for trade automation

This framework is designed to have 
- flexibility in design of strategies (concepts of Macro and Micro strategies are introduced)
- to be linked to any exchange/broker interface
However, as of now it is limited to interact only to Interactive Brokers TWS. 
As a consequence, the 'JOrder' is a close copy of 'order' from IB.

Project files are setup for NetBeans.

Central Main() is in IBLink.java

Things to do (for those who wants to continue development of it):
- Framework perhaps can run on your computer (see Config xml files to change directories)
- Ensure it runs with current TWS/IB (might require change of IB wrapper)

Some description and respective presentation is here:

http://www.innovaest.org/consulting/white-papers/tradeautomationplatformmadeinjava

Installation:
- project is under NetBeans. Eclipse can run as well
- install TWS
- install TWS API (https://interactivebrokers.github.io/#)
  * goto "TSW API\samples\Java" directory and copy those samples into new project.
  * when it asks to add Jar files, add from "TWS API\source\JavaClient" two jar-files: TwsApi.jar and TwsApi_debug.jar
  * this sample is useful to test your connection/problems. Run ApiDemo.java when you are not sure if any of your java apps is linking to TWS

Run NZTRADER:
- Within TWS Settings -> API/Settings "Enable ActiveX and Socket Clients"
- within Configurer/mainConfig.xml make sure that wanted security is listed. Examples are given.
- run AlgoTrader/Dispatcher.java 
- click 'IB connect' (low/left corner). next to it the red box will flash green if connection is established
  * in the Output window you must see something like "1 2104 Market data farm connection is OK:usfuture"
  * in right upper window click "Active Feed" to activate feed for particular instrument (stock, futures etc.)
  * Same for Strategy (make sure you select right strategy within IBLink.main)
- some example strategies are provided.
  
  
