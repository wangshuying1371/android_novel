package xd.wsy.constants;

public class Novel17BookDetail {
    public String name; // 名称
    public String author; // 作者
    public String type;  //书籍分类
    public String description; // 简介
    public String lastUpdateTime; // 最后更新
    public String lastChapter;   //最后章节

    public String pictureSrc; // 图标URL
    public String url;   //书籍详情页地址

    @Override
    public String toString() {
        return "name:" + name + "type" + type + " lastUpdateTime:" + lastUpdateTime + "lastChapter" + lastChapter + " description:" + description;
    }

}
