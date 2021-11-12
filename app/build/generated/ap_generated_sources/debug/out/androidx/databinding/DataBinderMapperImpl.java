package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new au.com.australiandroid.androidclient.DataBinderMapperImpl());
  }
}
