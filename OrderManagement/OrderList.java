import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class OrderList {
	 private final int MAX_NUM = 100;
	 private int _numOrder = 0;
	 private Order[] _orders = new Order[MAX_NUM];
	 
	 OrderList() {}
	 
	 OrderList(String orderFileName) {
		 loadOrders(orderFileName);
	 }
	 
	 public void print() {
		 for (int i = 0; i < _numOrder; i++) {
			 _orders[i].print(System.out);
		 }
	 }
	 
	 public void print(PrintStream ps) {
		 for (int i = 0; i < _numOrder; i++) {
			 _orders[i].print(ps);
		 }
	 }
	 
	 public int numOrders() {
		 return _numOrder;
	 }
	 
	 public Order getOrder(int i) {
		 return _orders[i];
	 }
	 
	 public void addOrder(String order) {
         try {
            _orders[_numOrder] = new Order(order);
            _numOrder++;
         } catch(Exception e) {
             System.out.println(e.getMessage());
         }
	 }
	 
	 public void loadOrders(String orderFileName) {
		 File file = new File(orderFileName);
		 Scanner input;
		 try {
			 input = new Scanner(file);
		 } catch(Exception e) {
			 System.out.println("Unknwon OrderList data File"); // 언노운 철자 틀림
			 return;
		 }
		 while (input.hasNext()) {
			 String line = input.nextLine();
			 line = line.trim();
			 if (line.length() > 0 &&
				!(line.charAt(0) == '/' && line.charAt(1) == '/')) { // no comment
				 try {
					 _orders[_numOrder] = new Order(line);
					 _numOrder++; // 오류 시 증가하지 않음->print에서 오류 문장은 출력되지 않음
				 } catch(Exception e) {
					 //System.out.println(e.getMessage());
					 _numOrder = 0; //지금까지 나온 정상적인 것들을 전부 무시해버림
					 return; // 밑의 정상적인 출력은 에러가 있을 시 출력을 하지 않기 위해 중간에 끝내버림
				 }		
			 }
		 }
	 }
	 
	 //untested
	 public void saveOrders(String orderFileName){
		 try {
			 File file = new File(orderFileName);
			 if (!file.exists()) {
				 file.createNewFile();
			 }
			 print(new PrintStream(file));
		 } catch(Exception e) {
			 System.out.println(e.getMessage());
			 return;
		 }
		 
	 }
	
	 public void sortByDate() {
         for (int i = 0; i < _numOrder-1; i++) {
             String[] f1 = _orders[i].get();
             for (int j = i+1; j < _numOrder; j++) {
                 String[] f2 = _orders[j].get();
                 if (f1[2].compareTo(f2[2]) > 0) { // compare date fields
                     Order tmp = _orders[i];
                     _orders[i] = _orders[j];
                     _orders[j] = tmp;
                 }
             }
         }
	 }
	
	 public void sortByCustomer() {
         for (int i = 0; i < _numOrder-1; i++) {
             String[] f1 = _orders[i].get();
             for (int j = i+1; j < _numOrder; j++) {
                 String[] f2 = _orders[j].get();
                 if (f1[1].compareTo(f2[1]) > 0) { // compare name fields
                     Order tmp = _orders[i];
                     _orders[i] = _orders[j];
                     _orders[j] = tmp;
                 }
             }
         }
	 }
}

