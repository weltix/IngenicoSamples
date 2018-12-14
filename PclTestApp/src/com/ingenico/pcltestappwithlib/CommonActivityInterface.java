package com.ingenico.pcltestappwithlib;

abstract interface CommonActivityInterface 
{
	abstract void onBarCodeReceived(String barCodeValue, int symbology);
	abstract void onBarCodeClosed();
	abstract void onStateChanged(String state);
}
