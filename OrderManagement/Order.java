import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Order {
	private final int MAX_ITEM = 100;
	private int _numItem = 0;
	
	private String _id;
	private String _name;
	private String _time;
	private int _price;
	private Item[] _items = new Item[MAX_ITEM];
	private String _location;
	
	// 따로 상속받은 이유: 메시지를 구분하기 위함
	private class TimeException extends Exception {}
	private class ItemException extends Exception {}
	
	Order(String[] fields) throws Exception { //requirement 사항: 배열에 들어오면 바로 call해서 끝냄, 생성자->생성자는 불가능하므로 set이라는 private method를 사용해 넘김
		set(fields);
	}
	
	Order(String str) throws Exception {
		String[] tok = str.split("::");
		for (int i = 0; i < tok.length; i++) tok[i] = tok[i].trim();
		try {
			set(tok);
		} catch(ItemException e) {
			System.out.println("		-- No ordered items -- invalid Order info line: " + str);
			throw new Exception(); //OrderList에서 exception을 다시 받을 수 있도록 throw를 사용
		} catch(TimeException e) {
			System.out.println("		-- No time order is issued -- invalid Order info line: " + str);
			throw new Exception(); //OrderList에서 exception을 다시 받을 수 있도록 throw를 사용
		}
	}
	
	public void print() { //print 콘솔 출력용
		print(System.out);
	}
	
	public void print(PrintStream ps) {
		ps.printf("%s :: %s :: %s :: %d ::", _id, _name, _time, _price);
		for (int i = 0; i < _numItem; i++) {
			_items[i].print(ps);
			if (i < _numItem-1) ps.printf(" : ");
		}
		ps.printf(" :: %s%n", _location);
	}
	
    public String[] get() {
        String[] fields = new String[5];
        fields[0] = _id;
        fields[1] = _name;
        fields[2] = _time;
        fields[3] = String.valueOf(_price);
        fields[4] = _location;
        return fields;
    }

    public String[] getItem(int i) {
        if (i < _numItem) {
            String[] fields = new String[4];
            fields[0] = _items[i]._id;
            fields[1] = _items[i]._name;
            fields[2] = String.valueOf(_items[i]._price);
            fields[3] = _items[i]._status;
            return fields;
        }
        return null;
    }

    public void setAddress(String addr) {
        _location = addr;
    }

    public void setItemStatus(int i, String status) {
        if (i < _numItem) _items[i]._status = status;
    }

	private void set(String[] fields) throws Exception {
		String[] tok = fields[4].split(":");
		_numItem = 0;
		for (int i = 0; i < tok.length; i++) { //이게 먼저 있어야 아래 price_sum 만들 수 있음
			_items[i] = new Item(tok[i]);
			_numItem++; //오류가 아닌 Item만 셈, 오류 시 증가하지 않음
		}
		
		LocalDateTime now = LocalDateTime.now();
		if (fields[0] == "" || fields[0].length() == 0) {
			_id = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		} else {
			_id = fields[0];
		}
		
		_name = fields[1];
		
		if (fields[2] == "" || fields[2].length() == 0) {
			_time = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm"));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm"); // https://stackoverflow.com/questions/20231539/java-check-the-date-format-of-current-string-is-according-to-required-format-or
			sdf.setLenient(false);
			try {
				Date date = sdf.parse(fields[2]);
				_time = fields[2];
			} catch (ParseException e) {
				System.out.println("Date Format Error --  " + fields[2]);
				throw new TimeException();
			}
		}
		
		int price_sum = 0;
		for (int i = 0; i < _numItem; i++) {
			price_sum += _items[i]._price;
		}
		if (fields[3] == "" || fields[3].length() == 0) {
			_price = price_sum;
		} else {
			_price = Integer.parseInt(fields[3]);
			if (_price != price_sum) {
				System.out.println("ERROR: price mismatched");
			}
		}
		
		_location = fields[5];
	}
	
	private class Item {
		public String _id;
		public String _name;
		public int _price;
		public String _status;

		Item(String str) throws Exception {
			String[] tok = str.split(";");
			if (tok.length != 4) {
				System.out.println("wrong ordered-item desciption format -- " + str);
				throw new ItemException(); // 어디가 에러인지 모르니까 일단 다 보내봄
			}

			for (int i = 0; i < tok.length; i++) tok[i] = tok[i].trim();
			_id 	= tok[0];
			_name 	= tok[1];
			_price 	= Integer.parseInt(tok[2]);
			_status = tok[3];
		}

		public void print(PrintStream ps) {
			ps.printf("%s;%s;%d;%s", _id, _name, _price, _status);			
		}
	}
}

