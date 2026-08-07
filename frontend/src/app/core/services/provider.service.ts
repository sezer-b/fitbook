import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Category, CreateCourseRequest, ProviderCourse, SlotBooking} from "../models/provider.model";
import {CourseSlot} from "../models/course.model";

@Injectable({providedIn: 'root'})
export class ProviderService {
    private readonly http = inject(HttpClient);
    private readonly base = '/api/provider/courses';

    getMyCourses(): Observable<ProviderCourse[]> {
        return this.http.get<ProviderCourse[]>(this.base);
    }

    createCourse(request: CreateCourseRequest): Observable<ProviderCourse> {
        return this.http.post<ProviderCourse>(this.base, request);
    }

    publishCourse(id: string): Observable<void> {
        return this.http.post<void>(`${this.base}/${id}/publish`, {});
    }

    cancelCourse(id: string): Observable<void> {
        return this.http.post<void>(`${this.base}/${id}/cancel`, {});
    }

    uploadImage(courseId: string, file: File): Observable<string> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${this.base}/${courseId}/images`, formData, {responseType: 'text'});
    }

    deleteImage(courseId: string, imageId: string): Observable<void> {
        return this.http.delete<void>(`${this.base}/${courseId}/images/${imageId}`);
    }

    getSlotBookings(slotId: string): Observable<SlotBooking[]> {
        return this.http.get<SlotBooking[]>(`${this.base}/slots/${slotId}/bookings`);
    }

    getCategories(): Observable<Category[]> {
        return this.http.get<Category[]>('/api/categories');
    }

    getCourseSlots(courseId: string): Observable<CourseSlot[]> {
        return this.http.get<CourseSlot[]>(`${this.base}/${courseId}/slots`);
    }
}