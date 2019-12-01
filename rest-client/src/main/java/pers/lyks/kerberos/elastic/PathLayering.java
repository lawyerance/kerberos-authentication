package pers.lyks.kerberos.elastic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lawyerance
 * @version 1.0 2019-11-30
 */
public class PathLayering {
    public static final String PATH_SEPARATOR = "/";

    private List<String> item;
    private int size;

    public PathLayering(String path) {
        String[] array = path.split(PATH_SEPARATOR);
        this.item = Stream.of(array).skip(1).collect(Collectors.toList());
        this.size = this.item.size();
    }

    public int getSize() {
        return size;
    }

    public void update(int index, String value) {
        item.set(index, value);
    }

    public void delete(int index) {
        item.remove(index);
    }

    @Override
    public String toString() {
        return PATH_SEPARATOR + item.stream().collect(Collectors.joining(PATH_SEPARATOR));
    }
}
