package com.ingenico.spicesshop;

public class struct_spice {
		
		public String name;
		public float price;
		public int quantity;

	public struct_spice(String t_name, float i_price, int i_quantity){
		name = t_name;
		price = i_price;
		quantity = i_quantity;
	}
}