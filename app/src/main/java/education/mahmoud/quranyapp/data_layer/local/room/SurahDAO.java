package education.mahmoud.quranyapp.data_layer.local.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SurahDAO {

    @Insert
    public void addSurah(SuraItem suraItem);

    @Update
    public void updateSurah(SuraItem suraItem);

    @Query("select  * from surah ")
    public List<SuraItem> getAllSurah();

    @Query("select name from surah ")
    public List<String> getAllSurahNames();

    @Query("select  * from surah where `index` = :id")
    public SuraItem getSurahByIndex(int id);

    @Query("select  * from surah where `name` = :name")
    public SuraItem getSurahByName(String name);

    @Query("select startIndex from surah where `index` = :index")
    int getSuraStartpage(int index);
}
