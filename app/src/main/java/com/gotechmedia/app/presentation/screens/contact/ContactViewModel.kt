package com.gotechmedia.app.presentation.screens.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.model.ContactInquiry
import com.gotechmedia.app.domain.usecase.SubmitContactUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContactSubmissionState {
    data object Idle : ContactSubmissionState
    data object Submitting : ContactSubmissionState
    data object Success : ContactSubmissionState
    data class Error(val message: String) : ContactSubmissionState
}

class ContactViewModel(
    private val submitContactUseCase: SubmitContactUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _subject = MutableStateFlow("")
    val subject: StateFlow<String> = _subject.asStateFlow()

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    private val _submissionState = MutableStateFlow<ContactSubmissionState>(ContactSubmissionState.Idle)
    val submissionState: StateFlow<ContactSubmissionState> = _submissionState.asStateFlow()

    fun setName(v: String) { _name.value = v }
    fun setEmail(v: String) { _email.value = v }
    fun setSubject(v: String) { _subject.value = v }
    fun setMessage(v: String) { _message.value = v }

    fun submit() {
        val n = _name.value.trim()
        val e = _email.value.trim()
        val s = _subject.value.trim()
        val m = _message.value.trim()

        if (n.isEmpty()) {
            _submissionState.value = ContactSubmissionState.Error("Please enter your name.")
            return
        }
        if (e.isEmpty() || !e.contains("@")) {
            _submissionState.value = ContactSubmissionState.Error("Please enter a valid email address.")
            return
        }
        if (m.isEmpty()) {
            _submissionState.value = ContactSubmissionState.Error("Please write your message.")
            return
        }

        viewModelScope.launch {
            _submissionState.value = ContactSubmissionState.Submitting
            val inquiry = ContactInquiry(
                name = n,
                email = e,
                subject = if (s.isBlank()) "General Inquiry" else s,
                message = m
            )
            val result = submitContactUseCase(inquiry)
            when (result) {
                is Resource.Success -> {
                    _submissionState.value = ContactSubmissionState.Success
                }
                is Resource.Error -> {
                    _submissionState.value = ContactSubmissionState.Error(result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun reset() {
        _submissionState.value = ContactSubmissionState.Idle
        _name.value = ""
        _email.value = ""
        _subject.value = ""
        _message.value = ""
    }
}
