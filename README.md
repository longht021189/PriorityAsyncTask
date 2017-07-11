Thực thi Runnable có độ ưu tiên.

Khởi tạo lớp Manager:
  Manager manager = new Manager(4, new Handler(), Executors.newFixedThreadPool(4));
  
Thực thi lớp Runnable:
  manager.execute(new Runnable() {
    @Override
    public void run() {
      ...
    }
  });
  
Priority chỉ được xét khi sử dụng lớp Scheduler
