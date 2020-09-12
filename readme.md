JDK NIO网络模型改进

- [x] 非阻塞式I/O模型
- [x] 弹性伸缩能力强
- [x] 单线程节省资源



NIO核心

- [x] Channel 通道
  - [ ] 特性：双向性、非阻塞性、操作唯一性
  - [ ] 实现：文件类(FileChannel)、UDP类(DatagramChannel)、TCP类(ServerSocketChannel/SocketChannel)
- [x] Buffer 缓冲区
  - [ ] 读写Channel中的数据、本质是一块内存空间
  - [ ] Buffer属性：Capactity容量、Position位置、Limit上限、Mark标记
- [x] Selector 选择器 或者 多路复用器
  - [ ] I/O就绪选择、NIO网络编程基础
  - [ ] SelectionKey: 四种就绪状态常量

NIO网络编程缺陷：Selector空轮询，导致CPU100%，官方声称JDK1.6已修复，但实际 JDK1.8依然存在



项目工程：多人聊天室