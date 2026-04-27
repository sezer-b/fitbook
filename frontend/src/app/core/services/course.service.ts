import {Injectable, inject} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CourseCard, CourseDetail} from '../models/course.model';

@Injectable({providedIn: 'root'})
export class CourseService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = '/api/courses';

    browse(lat: number, lng: number, radiusKm = 10, categoryId?: string): Observable<CourseCard[]> {
        let params = new HttpParams()
            .set('lat', lat)
            .set('lng', lng)
            .set('radiusKm', radiusKm);
        if (categoryId) params = params.set('categoryId', categoryId);
        return this.http.get<CourseCard[]>(this.baseUrl, {params});
    }

    getDetail(id: string, lat: number, lng: number, from?: string): Observable<CourseDetail> {
        let params = new HttpParams()
            .set('lat', lat)
            .set('lng', lng);
        if (from) params = params.set('from', from);
        return this.http.get<CourseDetail>(`${this.baseUrl}/${id}`, {params});
    }
}