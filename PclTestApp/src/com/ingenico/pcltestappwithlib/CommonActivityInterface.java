package com.ingenico.pcltestappwithlib;

interface CommonActivityInterface {
    void onBarCodeReceived(String barCodeValue, int symbology);

    void onBarCodeClosed();

    void onStateChanged(String state);
}