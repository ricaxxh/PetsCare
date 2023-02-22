package com.petscare.org.vista.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import com.petscare.org.R;
import com.petscare.org.databinding.ActivityMenuBinding;
import com.petscare.org.viewmodel.ViewModelMenu;
import com.petscare.org.vista.Interfaces.OnFragmentNavigationListener;
import com.petscare.org.vista.fragments.menu.FragmentDispositivos;
import com.petscare.org.vista.fragments.menu.FragmentFeed;
import com.petscare.org.vista.fragments.menu.FragmentMascotas;
import com.petscare.org.vista.fragments.menu.FragmentPerfil;
import com.petscare.org.vista.fragments.menu.FragmentServicios;

public class ActivityMenu extends AppCompatActivity implements OnFragmentNavigationListener {

    private ViewModelMenu vmMenu;
    private ActivityMenuBinding binding;

    private int frag_index = 0;
    private FragmentTransaction transaction;
    private FragmentFeed frag_feed;
    private FragmentMascotas frag_mascotas;
    private FragmentServicios frag_servicios;
    private FragmentDispositivos frag_dispositivos;
    private FragmentPerfil frag_perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.THEME_TOOLBAR_ACTIVITY);
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vmMenu = new ViewModelProvider(this).get(ViewModelMenu.class);

        crearFragments();
        observar_ldata();
        mostrarFragment(frag_index);
        eventosUI();

    }

    private void observar_ldata() {
        frag_index = vmMenu.Data().getIndex();
    }

    private void eventosUI() {
        binding.bottomNavigationMenu.setOnItemSelectedListener( item ->{
            if (item.getItemId() == R.id.frag_feed){
                mostrarFragment(0);
                return true;
            } else if (item.getItemId() == R.id.frag_mascotas){
                mostrarFragment(1);
                return true;
            } else if (item.getItemId() == R.id.frag_servicios){
                mostrarFragment(2);
                return true;
            } else if (item.getItemId() == R.id.frag_dispositivos){
                mostrarFragment(3);
                return true;
            } else  if (item.getItemId() == R.id.frag_perfil){
                mostrarFragment(4);
                return true;
            }
            return false;
        });
    }

    private void crearFragments() {
        transaction = getSupportFragmentManager().beginTransaction();
        frag_feed = new FragmentFeed();
        frag_mascotas = new FragmentMascotas();
        frag_servicios = new FragmentServicios();
        frag_dispositivos = new FragmentDispositivos();
        frag_perfil = new FragmentPerfil();
    }

    @Override
    public void mostrarFragment(int index) {
        transaction = getSupportFragmentManager().beginTransaction();
        switch (index){
            case 0:
                transaction.replace(R.id.contenedor_frags_menu,frag_feed).commit();
                break;

            case 1:
                transaction.replace(R.id.contenedor_frags_menu,frag_mascotas).commit();
                break;

            case 2:
                transaction.replace(R.id.contenedor_frags_menu,frag_servicios).commit();
                break;

            case 3:
                transaction.replace(R.id.contenedor_frags_menu,frag_dispositivos).commit();
                break;

            case 4:
                transaction.replace(R.id.contenedor_frags_menu,frag_perfil).commit();
                break;
        }
        vmMenu.Data().setIndex(index);
    }
}