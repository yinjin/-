# Page snapshot

```yaml
- generic [ref=e3]:
  - banner [ref=e4]:
    - navigation [ref=e5]:
      - link "Home" [ref=e6] [cursor=pointer]:
        - /url: /
      - link "About" [ref=e7] [cursor=pointer]:
        - /url: /about
  - main [ref=e8]:
    - generic [ref=e10]:
      - generic [ref=e12]:
        - heading "好才管理系统" [level=2] [ref=e13]
        - paragraph [ref=e14]: 用户登录
      - generic [ref=e16]:
        - generic [ref=e17]:
          - generic [ref=e18]: "* 用户名"
          - textbox "* 用户名" [ref=e22]:
            - /placeholder: 请输入用户名
            - text: admin
        - generic [ref=e23]:
          - generic [ref=e24]: "* 密码"
          - generic [ref=e27]:
            - textbox "* 密码" [ref=e28]:
              - /placeholder: 请输入密码
              - text: admin123
            - img [ref=e31] [cursor=pointer]
        - generic [ref=e35] [cursor=pointer]:
          - generic [ref=e36]:
            - checkbox "记住用户名"
          - generic [ref=e38]: 记住用户名
        - button "登录" [ref=e41] [cursor=pointer]:
          - generic [ref=e42]: 登录
```