1、运行参数 java -jar depAnalyzeTool.jar -c/-m projectPath...
-c表示无配置文件的架构抽取，-m表示有配置文件的架构匹配（配置文件默认位于项目根目录，文件名为project-structure.yaml）
projectPath为待分析项目的路径，可以同时分析多个项目
2、以json格式输出分析结果。文件名为 项目名称-cluster/match.json，文件路径为 当前目录/output 文件夹下。
3、架构匹配的样例项目地址，包括配置文件样例 https://github.com/icecream154/OOAD_lab2