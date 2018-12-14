package com.ingenico.spicesshop;

abstract interface CommonActivityInterface 
{
	//abstract void onBarCodeReceived(Integer barCodeValue);
	abstract void onBarCodeReceived(String barCodeValue, int symbology);
	abstract void onBarCodeClosed();
	abstract void onStateChanged(String state);
}
