package com.example.agriassist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.agriassist.databinding.FragmentAccountBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()

        binding.saveProfileButton.setOnClickListener {
            saveFarmInfo()
        }

        binding.logoutButton.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("userName", "User")
        val farmName = sharedPreferences.getString("farmName", "")
        val phone = sharedPreferences.getString("emergencyPhone", "")

        binding.usernameText.text = name
        binding.editFarmName.setText(farmName)
        binding.editPhone.setText(phone)
    }

    private fun saveFarmInfo() {
        val farmName = binding.editFarmName.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()

        val sharedPreferences = requireContext().getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("farmName", farmName)
        editor.putString("emergencyPhone", phone)
        editor.apply()

        Toast.makeText(requireContext(), "Farm information updated!", Toast.LENGTH_SHORT).show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .show()
    }

    private fun performLogout() {
        val sharedPreferences = requireContext().getSharedPreferences("AgriAssistPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
