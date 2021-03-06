package education.mahmoud.quranyapp.Util;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import education.mahmoud.quranyapp.data_layer.Repository;
import education.mahmoud.quranyapp.data_layer.local.room.AyahItem;
import education.mahmoud.quranyapp.data_layer.local.room.SuraItem;
import education.mahmoud.quranyapp.data_layer.model.full_quran.Ayah;
import education.mahmoud.quranyapp.data_layer.model.full_quran.Surah;
import education.mahmoud.quranyapp.feature.show_sura_ayas.Page;
import education.mahmoud.quranyapp.model.Quran;
import education.mahmoud.quranyapp.model.Sura;

public class App extends Application {

    private static final String TAG = "App";
    private Repository repository;
    private List<Page> fullQuranPages;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = Repository.getInstance(this);
        int ahays = repository.getTotlaAyahs();
        Log.d(TAG, "onCreate:  nbbbbbb  ahyas" + ahays);
        if (ahays == 0) {
          //  loadQuran();
            persistanscePages();
        } else {
            persistanscePages();
        }

    }

    private void persistanscePages() {
        new Thread(() -> {
            try {
                loadFullQuran();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void loadQuran() {
        Log.d(TAG, "loadQuran: ");
        List<Surah> surahs = Util.getFullQuranSurahs(this);
        StoreInDb(surahs);
    }

    private void StoreInDb(List<Surah> surahs) {
        new Thread(() -> {
            Store(surahs);
        }).start();
    }

    private void Store(List<Surah> surahs) {

        SuraItem suraItem;
        AyahItem ayahItem;

        Quran quran = Util.getQuranClean(this);
        Sura[] surahClean = quran.getSurahs();

        String clean;
        for (Surah surah : surahs) {
            suraItem = new SuraItem(surah.getNumber()
                    , surah.getAyahs().size()
                    , surah.getName(), surah.getEnglishName()
                    , surah.getEnglishNameTranslation(), surah.getRevelationType());
            // add start page
            suraItem.setIndex(surah.getNumber());
            suraItem.setStartIndex(surah.getAyahs().get(0).getPage());
            try {
                repository.addSurah(suraItem);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Ayah ayah : surah.getAyahs()) {
                ayahItem = new AyahItem(ayah.getNumber(), surah.getNumber()
                        , ayah.getPage(), ayah.getJuz()
                        , ayah.getHizbQuarter(), false
                        , ayah.getNumberInSurah(), ayah.getText()
                        , ayah.getText());

                //    ayahItem.setTextClean(Util.removeTashkeel(ayahItem.getText()));
                if (surahClean != null) {
                    clean = surahClean[surah.getNumber() - 1].getAyahs()[ayahItem.getAyahInSurahIndex() - 1].getText();
                    ayahItem.setTextClean(clean);
                } else {
                    ayahItem.setTextClean(Util.removeTashkeel(ayahItem.getText()));
                }
                try {
                    repository.addAyah(ayahItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

    }

    private void loadFullQuran() {
        List<Page> pages = new ArrayList<>();
        Page page;
        List<AyahItem> ayahItems;
        for (int i = 1; i <= 604; i++) {
            ayahItems = repository.getAyahsByPage(i);
            if (ayahItems.size() > 0) {
                page = new Page();
                page.setAyahItems(ayahItems);
                page.setPageNum(i);
                page.setJuz(ayahItems.get(0).getJuz());
                pages.add(page);
            }
        }

        fullQuranPages = new ArrayList<>(pages);

    }


    public Repository getRepository() {
        return repository;
    }

    public List<Page> getQuranPages() {
        return fullQuranPages;
    }


}
