package xd.wsy.constants;

public class BiQuGeBookDetail {
    public String name; // 名称
    public String author; // 作者
    public String type;  //书籍分类
    public String description; // 简介
    public String lastUpdateTime; // 最后更新
    public String lastChapter;   //最后章节

    public String src; // 图标URL
    public String url;
    public String recommend;  //推荐

    @Override
    public String toString() {
        return "name:" + name + " author:" + author + "type" + type + " lastUpdateTime:" + lastUpdateTime + "lastChapter" + lastChapter + "recommend" + recommend + " description:" + description;
    }

}
